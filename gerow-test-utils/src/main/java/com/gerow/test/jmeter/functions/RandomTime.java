package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Component
public class RandomTime extends AbstractFunction {

    private CompoundVariable start;

    private CompoundVariable end;

    private CompoundVariable interval;

    private CompoundVariable format;

    private static final String KEY = "__RandomTime";

    private CompoundVariable startVarName;
    private CompoundVariable endVarName;

    private String resultStart;

    private String resultEnd;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        build();
        TestUtils.saveVariables(startVarName, resultStart);
        TestUtils.saveVariables(endVarName, resultEnd);
        return resultStart;
    }

    private void build() {
        String fm = format.execute().trim();
        if (StringUtils.isBlank(fm)) {
            fm = "HH:mm:ss";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fm);
        LocalTime start = LocalTime.parse(this.start.execute().trim(), formatter);
        LocalTime end = LocalTime.parse(this.end.execute().trim());
        int interval = Integer.valueOf(this.interval.execute().trim());
        LocalTime resultStart = start.plusSeconds(interval * getRandom(start, end, interval));
        LocalTime resultEnd = resultStart.plusSeconds(interval * (getRandom(resultStart, end, interval) + 1));
        this.resultStart = resultStart.format(formatter);
        this.resultEnd = resultEnd.format(formatter);
    }

    private int getRandom(LocalTime start, LocalTime end, int interval) {
        long seconds = Duration.between(start, end).getSeconds();
        Long i1 = seconds / interval;
        return new Random().nextInt(i1.intValue());
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 6, 6);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.start = compoundVariables[0];
        this.end = compoundVariables[1];
        this.interval = compoundVariables[2];
        this.format = compoundVariables[3];
        this.startVarName = compoundVariables[4];
        this.endVarName = compoundVariables[5];
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
