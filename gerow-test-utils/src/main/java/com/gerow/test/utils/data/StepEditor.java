package com.gerow.test.utils.data;

import com.gerow.test.task.ITestStep;
import com.gerow.test.utils.assertion.Assertion;
import com.gerow.test.utils.json.JsonPathUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Data
public class StepEditor {
    /**
     * jsonPath
     */
    private String key;
    /**
     * 值
     */
    private Object value;
    /**
     * 预期响应code
     */
    private int code;
    /**
     * 预期响应msg
     */
    private String msg;
    /**
     * 测试名称
     */
    private String name;

    private List<Assertion> assertion = new ArrayList<>();
    /**
     * 条件字段
     */
    private LinkedHashMap<String, Object> json = new LinkedHashMap<>();

    /**
     * 构造请求体
     *
     * @param body 默认请求体
     * @return 替换参数后得请求体
     */
    public String builderBody(String body, ITestStep testStep) {
        if (StringUtils.isNotBlank(key)) {
            body = JsonPathUtils.put(body, key, value);
        }
        for (String key : json.keySet()) {
            Object value = json.get(key);
            try {
                if (value instanceof String) {
                    if (((String) value).contains("__BeanShell")) {
                        body = JsonPathUtils.put(body, key, new BigDecimal(testStep.replace((String) value)));
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (value instanceof String && ((String) value).contains("__RandomOrderId")) {
                value = testStep.replace((String) value);
            }
            body = JsonPathUtils.put(body, key, value);
        }
        return body;
    }

    /**
     * 构造断言
     *
     * @return 断言
     */
    public List<Assertion> builderAssertion() {
        List<Assertion> assertions = assertion;
        if (StringUtils.isNotBlank(this.msg)) {
            Assertion msg = new Assertion();
            msg.setKey("$.msg");
            msg.setValue(this.msg);
            assertions.add(msg);
        }
        if (this.code != 0) {
            Assertion code = new Assertion();
            code.setKey("$.code");
            code.setValue(this.code);
            assertions.add(code);
        }
        return assertions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StepEditor that = (StepEditor) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, code, msg, name, assertion, json);
    }
}
