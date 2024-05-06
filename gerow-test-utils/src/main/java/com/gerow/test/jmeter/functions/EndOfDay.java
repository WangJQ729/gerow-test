package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

public class EndOfDay extends AbstractFunction {


    private static final String KEY = "__EndOfDay";

    private CompoundVariable varName;

    private CompoundVariable index;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        int i = 0;
        if (index != null) {
            i = Integer.parseInt(index.execute());
        }
        LocalDateTime now = LocalDateTime.now().plusDays(i);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
        long milli = end.toInstant(ZoneOffset.of("+8")).getEpochSecond();
        if (varName != null && StringUtils.isNotBlank(varName.execute())) {
            TestUtils.saveVariables(varName, Long.toString(milli));
        }
        return Long.toString(milli);
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        if (compoundVariables.length > 1) {
            this.index = compoundVariables[0];
            this.varName = compoundVariables[1];
        } else if (compoundVariables.length > 0) {
            this.index = compoundVariables[0];
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
