package com.jq.test.utils;

import com.jq.test.json.JsonPathUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
    private List<Assertion> assertion = new ArrayList<>();
    private Map<String, Object> containsOnlyAssertion = new HashMap<>();

    /**
     * 测试名称
     */
    private String name;
    /**
     * 条件字段
     */
    private Map<String, Object> fields = new HashMap<>();

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
        for (String key : fields.keySet()) {
            body = JsonPathUtils.put(body, key, fields.get(key));
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
        for (String key : containsOnlyAssertion.keySet()) {
            Assertion assertion = new Assertion();
            assertion.setAssertionType(AssertionType.CONTAINSONLY);
            assertion.setKey(key);
            assertion.setValue(this.containsOnlyAssertion.get(key));
            assertions.add(assertion);
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
        return Objects.hash(key, value, assertion, containsOnlyAssertion, name, fields);
    }
}
