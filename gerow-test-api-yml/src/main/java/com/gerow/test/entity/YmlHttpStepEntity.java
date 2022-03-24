package com.gerow.test.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gerow.test.task.ITestMethod;
import com.gerow.test.utils.data.DataType;
import com.gerow.test.utils.data.Extractor;
import com.gerow.test.utils.data.StepEditor;
import com.gerow.test.task.ITestStep;
import com.gerow.test.task.YmlTestStep;
import com.gerow.test.utils.assertion.Assertion;
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
    private Map<String, String> file = new HashMap<>();
    /**
     * 等待时间
     */
    private String sleep = "0";
    /**
     * 直到成功的最晚时间
     * 这个时间内回一直
     */
    private int untilWait = 0;
    private long intervals = 1000;


    private Map<String, String> iter = new HashMap<>();
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
    private List<Extractor> extractor = new ArrayList<>();
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
    private List<StepEditor> editor = new ArrayList<>();

    private StepEditor bodyEditor;

    private DataType responseType = DataType.JSON;

    private boolean extractTaskState = false;

    /**
     * 构造测试步骤
     *
     * @param testMethod 测试方法
     * @param factory    字段检查构造参数
     * @return 测试步骤
     */
    public List<ITestStep> build(ITestMethod testMethod, StepEditor factory) {
        if (dataProvider.isEmpty()) {
            dataProvider = Collections.singletonList(new LinkedHashMap<>());
        }
        if (editor.isEmpty()) {
            editor = Collections.singletonList(new StepEditor());
        }
        List<ITestStep> result = new ArrayList<>();
        for (StepEditor checkFactory : editor) {
            StepEditor stepEditor;
            if (checkFactory.equals(new StepEditor())) {
                stepEditor = factory;
            } else {
                stepEditor = checkFactory;
            }
            for (int i = 0; i < invocationCount; i++) {
                for (Map<String, String> data : dataProvider) {
                    Map<String, String> map = new LinkedHashMap<>(data);
                    map.put("stepNum", String.valueOf(i));
                    YmlTestStep testStep = new YmlTestStep(this.copy(), map, testMethod, stepEditor);
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

    public Map<String, String> getHeaders() {
        if (headers == null) {
            return new HashMap<>();
        }
        return headers;
    }
}
