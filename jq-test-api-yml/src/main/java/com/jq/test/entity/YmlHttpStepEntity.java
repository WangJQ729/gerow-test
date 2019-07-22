package com.jq.test.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jq.test.task.YmlTestStep;
import com.jq.test.task.ITestMethod;
import com.jq.test.task.ITestStep;
import com.jq.test.utils.Assertion;
import com.jq.test.utils.FieldCheckFactory;
import com.jq.test.utils.FileInfo;
import com.jq.test.utils.SaveParam;
import lombok.Data;

import java.util.*;

@Data
public class YmlHttpStepEntity {
    /**
     * 步骤名称
     */
    private String name;
    /**
     * 所要的步骤的名称
     */
    private String byName;
    /**
     * host 默认从配置文件中拿
     */
    private String host;
    /**
     * 请求url
     */
    private String url;
    /**
     * 如果是上传文件填写
     */
    private FileInfo file;
    /**
     * 等待时间
     */
    private int sleep = 0;
    /**
     * 直到成功的最晚时间
     * 这个时间内回一直
     */
    private int untilWait = 0;
    /**
     * form表达
     */
    private Map<String, String> form = new HashMap<>();
    /**
     * 请求头参数
     */
    private Map<String, String> headers = new HashMap<>();
    /**
     * 请求方法
     */
    private String method;
    /**
     * url参数
     */
    private Map<String, String> variables = new HashMap<>();
    /**
     * 请求体
     */
    private String body;
    /**
     * 断言对象
     */
    private List<Assertion> assertion = new ArrayList<>();
    /**
     * 参数提取对象
     */
    private List<SaveParam> save = new ArrayList<>();
    /**
     * 参数化
     */
    private List<LinkedHashMap<String, String>> dataProvider = new ArrayList<>();
    /**
     * 执行次数（默认为1）
     */
    private int invocationCount = 1;
    /**
     * 字段检查对象
     */
    private List<FieldCheckFactory> fieldCheck = new ArrayList<>();

    private FieldCheckFactory bodyEditor;

    /**
     * 构造测试步骤
     *
     * @param testMethod 测试方法
     * @param factory    字段检查构造参数
     * @return 测试步骤
     */
    public List<ITestStep> build(ITestMethod testMethod, FieldCheckFactory factory) {
        if (dataProvider.isEmpty()) {
            dataProvider = Collections.singletonList(new LinkedHashMap<>());
        }
        if (fieldCheck.isEmpty()) {
            fieldCheck = Collections.singletonList(new FieldCheckFactory());
        }
        List<ITestStep> result = new ArrayList<>();
        for (FieldCheckFactory checkFactory : fieldCheck) {
            FieldCheckFactory fieldCheckFactory;
            if (checkFactory.equals(new FieldCheckFactory())) {
                fieldCheckFactory = factory;
            } else {
                fieldCheckFactory = checkFactory;
            }
            for (int i = 0; i < invocationCount; i++) {
                for (Map<String, String> data : dataProvider) {
                    Map<String, String> map = new LinkedHashMap<>(data);
                    map.put("stepNum", String.valueOf(i));
                    YmlTestStep testStep = new YmlTestStep(this.copy(), map, testMethod, fieldCheckFactory);
                    result.add(testStep);
                }
            }
        }
        return result;
    }

    /**
     * @return copy的测试步骤
     */
    public YmlHttpStepEntity copy() {
        return JSONObject.parseObject(JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue), YmlHttpStepEntity.class);
    }
}
