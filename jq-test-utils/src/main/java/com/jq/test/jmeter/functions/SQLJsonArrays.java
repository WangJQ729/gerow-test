package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class SQLJsonArrays extends SQLQuery {

    private static final String KEY = "__SQLJsonArrays";

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
//        Query query = entityManager.createNativeQuery(sql.execute().trim());
//        List results = query.getResultList();
//        JSONArray array = new JSONArray();
//        for (Object result : results) {
//            if (result instanceof Object[]) {
//                array.add(buildResult((Object[]) result));
//            }
//        }
//        return array.toJSONString();
        return "";
    }

    private JSONObject buildResult(Object[] result) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < fields.size(); i++) {
            Object value = result[i] == null ? StringUtils.EMPTY : result[i];
            String value1 = value == null ? StringUtils.EMPTY : value.toString();
            CompoundVariable key = fields.get(i);
            JMeterVariables variables = JMeterContextService.getContext().getVariables();
            String trim = key.execute().trim();
            if (StringUtils.isNoneBlank(variables.get(trim))) {
                if (value instanceof BigDecimal) {
                    value1 = new BigDecimal(variables.get(trim)).add((BigDecimal) value).toString();
                } else if (value instanceof BigInteger) {
                    value1 = new BigInteger(variables.get(trim)).add((BigInteger) value).toString();
                }
            }
            TestUtils.saveVariables(key, value1);
            jsonObject.put(trim, value);
        }
        return jsonObject;
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }
}
