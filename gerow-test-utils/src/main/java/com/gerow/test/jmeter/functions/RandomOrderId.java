package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomOrderId extends AbstractFunction {

    private CompoundVariable vars;

    private static final String KEY = "__RandomOrderId";

    private static final Random random = new Random();

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {

        String idNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + String.format("%04d", random.nextInt(9999));
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
