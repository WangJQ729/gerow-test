package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class Timestamp extends AbstractFunction {


    private static final String KEY = "__Timestamp";

    private CompoundVariable time;
    private CompoundVariable timeFormat;
    private CompoundVariable varName;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String execute = timeFormat.execute();
        if (StringUtils.isNotBlank(execute)) {
            format = DateTimeFormatter.ofPattern(execute);
        }
        LocalDateTime dateTime = LocalDateTime.parse(time.execute(), format);
        String milli = String.valueOf(dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        if (StringUtils.isNotBlank(varName.execute())) {
            TestUtils.saveVariables(varName, milli);
        }
        return milli;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 1, 3);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.time = compoundVariables[0];
        this.timeFormat = compoundVariables[1];
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
