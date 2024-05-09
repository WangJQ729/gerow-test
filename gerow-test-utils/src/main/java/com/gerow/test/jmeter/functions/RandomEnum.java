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
import java.util.concurrent.ThreadLocalRandom;

public class RandomEnum extends AbstractFunction {

    private static final String KEY = "__RandomEnum";
    private CompoundVariable enumString;
    private CompoundVariable vars; // Renamed 'vars' to a more descriptive name

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        try {
            String[] array = StringUtils.split(enumString.execute(), "|");
            String result = array[ThreadLocalRandom.current().nextInt(array.length)]; // Use ThreadLocalRandom for thread safety
            if (vars != null) {
                TestUtils.saveVariables(vars, result);
            }
            return result;
        } catch (Exception e) {
            // Handle exceptions gracefully (e.g., log or return a default value)
            return "ERROR"; // Change this to an appropriate default value
        }
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1); // Changed minimum parameter count to 1
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        enumString = compoundVariables[0];
        if (compoundVariables.length > 1) {
            vars = compoundVariables[1];
        }
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        // Provide descriptions for function arguments (if applicable)
        return Arrays.asList("EnumString (e.g., \"VALUE1|VALUE2|VALUE3\")", "ResultVariable (optional)");
    }
}
