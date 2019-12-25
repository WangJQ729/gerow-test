package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class RandomOrderId extends AbstractFunction {

    private CompoundVariable vars;

    private static final String KEY = "__RandomOrderId";

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {

        String idNo = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + RandomStringUtils.random(7, "0123456789");
        if (vars != null) {
            TestUtils.saveVariables(vars, idNo);
        }
        return idNo;
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 0);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        if (compoundVariables.length > 0) {
            vars = compoundVariables[0];
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
