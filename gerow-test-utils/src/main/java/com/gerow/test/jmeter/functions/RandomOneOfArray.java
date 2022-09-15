package com.gerow.test.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomOneOfArray extends AbstractFunction {

    protected CompoundVariable vars;

    private static final String KEY = "__RandomOneOfArray";
    private static final Random random = new Random();
    private CompoundVariable result;


    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        return result.execute();
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 2);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        if (compoundVariables.length > 0) {
            this.result = compoundVariables[random.nextInt(compoundVariables.length)];
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
