package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class PlusDate extends AbstractFunction {

    private static final String KEY = "__PlusDay";

    private CompoundVariable plusDay;

    private CompoundVariable format;

    private CompoundVariable varName;

    private String result;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        build();
        TestUtils.saveVariables(varName, result);
        return result;
    }

    private void build() {
        String fm = format.execute().trim();
        if (StringUtils.isBlank(fm)) {
            fm = "yyyy-MM-dd";
        }
        LocalDate result = LocalDate.now().plusDays(Integer.valueOf(this.plusDay.execute().trim()));
        this.result = result.format(DateTimeFormatter.ofPattern(fm));
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 3, 3);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.format = compoundVariables[0];
        this.plusDay = compoundVariables[1];
        this.varName = compoundVariables[2];
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
