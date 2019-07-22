package com.jq.test.utils;

import com.jq.test.json.JsonPathUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class FieldCheckFactory {
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
    /**
     * 条件字段
     */
    private List<ConditionField> fields = new ArrayList<>();

    /**
     * 构造请求体
     *
     * @param body 默认请求体
     * @return 替换参数后得请求体
     */
    public String builderBody(String body) {
        if (StringUtils.isNotBlank(key)) {
            body = JsonPathUtils.put(body, key, value);
        }
        for (ConditionField field : fields) {
            body = JsonPathUtils.put(body, field.getKey(), field.getValue());
        }
        return body;
    }

    /**
     * 构造断言
     *
     * @return 断言
     */
    public List<Assertion> builderAssertion() {
        List<Assertion> assertions = new ArrayList<>();
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
        FieldCheckFactory that = (FieldCheckFactory) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, code, msg, name);
    }
}
