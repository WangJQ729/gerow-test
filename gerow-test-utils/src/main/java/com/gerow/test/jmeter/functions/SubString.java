package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SubString extends AbstractFunction {

    protected CompoundVariable vars;

    private static final String KEY = "__SubString";
    private CompoundVariable str;
    private CompoundVariable start;
    private CompoundVariable end;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        String old = str.execute();
        int start = Integer.parseInt(this.start.execute());
        int end = Integer.parseInt(this.end.execute());
        String result = StringUtils.substring(old, start, end);
        if (vars != null ) {
            TestUtils.saveVariables(vars, result);
        }
        return result;
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 3, 4);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.str = compoundVariables[0];
        this.start = compoundVariables[1];
        this.end = compoundVariables[2];
        if (compoundVariables.length > 3) {
            this.vars = compoundVariables[3];
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
