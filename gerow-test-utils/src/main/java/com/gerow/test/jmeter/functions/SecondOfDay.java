package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

public class SecondOfDay extends AbstractFunction {


    private static final String KEY = "__SecondOfDay";

    private CompoundVariable addSecond;
    private CompoundVariable varName;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime of = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 0, 0);
        long between = ChronoUnit.SECONDS.between(of, LocalDateTime.now()) + Integer.parseInt(addSecond.execute());
        if (varName != null && StringUtils.isNotBlank(varName.execute())) {
            TestUtils.saveVariables(varName, Long.toString(between));
        }
        return Long.toString(between);
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.addSecond = compoundVariables[0];
        if (compoundVariables.length > 1) {
            this.varName = compoundVariables[1];
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
