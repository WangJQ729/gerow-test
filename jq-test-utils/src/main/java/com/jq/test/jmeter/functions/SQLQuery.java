package com.jq.test.jmeter.functions;

import com.alibaba.fastjson.JSONObject;
import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
public class SQLQuery extends AbstractFunction {

//    protected static EntityManager entityManager;

//    @PersistenceContext
//    public void setEntityManager(EntityManager manager) {
//        this.entityManager = manager;
//    }

    protected CompoundVariable sql;

    protected List<CompoundVariable> fields = new LinkedList<>();

    private static final String KEY = "__SQL";

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
//        Query query = entityManager.createNativeQuery(sql.execute().trim());
//        Object singleResult = query.getSingleResult();
//        if (singleResult instanceof Object[]) {
//            return buildResult((Object[]) singleResult).toJSONString();
//        } else {
//            TestUtils.saveVariables(fields.get(0), singleResult.toString());
//            return singleResult.toString();
//        }
        return "";
    }

    private JSONObject buildResult(Object[] result) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < fields.size(); i++) {
            String value = result[i] == null ? StringUtils.EMPTY : result[i].toString();
            TestUtils.saveVariables(fields.get(i), value == null ? StringUtils.EMPTY : value.toString());
            jsonObject.put(fields.get(i).execute().trim(), value);
        }
        return jsonObject;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 2);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        sql = compoundVariables[0];
        for (int i = 1; i < compoundVariables.length; i++) {
            fields.add(compoundVariables[i]);
        }
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return null;
    }
}
