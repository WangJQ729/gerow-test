package com.jq.test.utils;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.Option;
import com.jq.test.json.JsonPathUtils;
import com.jq.test.task.ITest;
import com.jq.test.task.ITestMethod;
import com.jq.test.task.ITestStep;
import io.qameta.allure.Allure;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class SaveParam {
    /**
     * 保存参数的位置
     */
    private Site site = Site.DEFAULT;
    /**
     * 数据来源类型
     */
    private DataType type = DataType.JSON;
    /**
     * 提取参数个数
     */
    private int size = 1;
    /**
     * 参数的key
     */
    private String name;
    /**
     * 值的path或者具体值
     */
    private String value;
    /**
     * 数据
     */
    private String data;
    /**
     * 数据类型参数的分隔符
     */
    private String separator = ",";
    /**
     * jsonPath配置
     */
    private Option[] options = new Option[]{};

    private Map<String, String> json = new HashMap<>();

    /**
     * 保存参数
     *
     * @param entity 响应实体
     * @param test   测试方法
     * @param <T>    响应体类型
     */
    public <T> void save(ResponseEntity<T> entity, ITestMethod test) {
        for (String key : json.keySet()) {
            SaveParam saveParam = build(key);
            saveParam.save(entity, test);
        }
        if (StringUtils.isNotBlank(name)) {
            Allure.step("获取参数:" + name, () -> doSave(entity, test));
        }
    }

    private <T> void doSave(ResponseEntity<T> entity, ITestMethod test) {
        ITest save = getTest(test);
        switch (type) {
            case DATA:
                saveJsonPath(save, JsonPathUtils.read(data, value, options));
                break;
            case JSON:
                if (StringUtils.isNotBlank(data)) {
                    saveJsonPath(save, JsonPathUtils.read(data, value, options));
                } else {
                    saveJsonPath(save, JsonPathUtils.read(entity.getBody(), value, options));
                }
                break;
            case HEADER:
                String value = entity.getHeaders().get(this.value).get(0);
                Allure.step(value);
                save.save(name, value);
                break;
            case CONSTANT:
                Allure.step(this.value);
                save.save(name, this.value);
                break;
            case XML:
            default:
                break;
        }
    }

    /**
     * 保存json类型的数据
     *
     * @param save   保存位置的实体对象
     * @param result 实际参数
     * @param <T>    参数的类型
     */
    private <T> void saveJsonPath(ITest save, T result) {
        if (result == null) {
            if (Arrays.asList(options).contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
                save.save(name, "");
            } else {
                Assertions.fail("请求响应中未找到：" + value);
            }
        } else {
            String value;
            if (result instanceof Collection) {
                value = buildListResult((Collection<?>) result);
            } else {
                value = result.toString();
            }
            Allure.step(value);
            save.save(name, value);
        }
    }

    /**
     * 构造结果数组
     *
     * @param result 结果
     * @return 结果数组的字符串
     */
    private String buildListResult(Collection<?> result) {
        String value;
        ArrayList<?> list = new ArrayList<>(result);
        Collections.shuffle(list);
        value = list.stream().limit(size).map(o -> {
            if (o instanceof Map) {
                return JSONObject.toJSONString(o);
            } else {
                return o.toString();
            }
        }).collect(Collectors.joining(separator));
        if (!Arrays.asList(options).contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
            Assertions.assertThat(value).as("参数未找到: " + this.value).isNotBlank();
        }
        return value;
    }

    /**
     * 保存参数
     *
     * @param test 保存的测试方法
     */
    public void save(ITestMethod test) {
        ITest save = getTest(test);
        for (String key : json.keySet()) {
            SaveParam saveParam = build(key);
            saveParam.save(test);
        }
        if (StringUtils.isNotBlank(name)) {
            Allure.step("获取参数:" + name, () -> save.save(name, value));
        }
    }

    /**
     * 获取所要保存的位置
     *
     * @param test 测试方法
     * @return 保存位置的实体对象
     */
    private ITest getTest(ITestMethod test) {
        ITest save = test;
        switch (site) {
            case SHEET:
            case DEFAULT:
            case TESTCLASS:
                save = test.getTestClass();
                break;
            case EXCEL:
            case ALL:
            case TESTSUIT:
                save = test.getTestClass().getTestSuite();
                break;
            case TESTCASE:
                break;
        }
        return save;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    private SaveParam build(String key) {
        SaveParam saveParam = new SaveParam();
        saveParam.setName(key);
        saveParam.setValue(json.get(key));
        saveParam.setData(data);
        saveParam.setOptions(options);
        saveParam.setSeparator(separator);
        saveParam.setSite(site);
        saveParam.setSize(size);
        saveParam.setType(type);
        return saveParam;
    }
}
