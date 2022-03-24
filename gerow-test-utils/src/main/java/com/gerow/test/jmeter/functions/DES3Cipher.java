package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import java.util.Collection;
import java.util.List;

public class DES3Cipher extends AbstractFunction {

    private static final String KEY = "__DES3Cipher";
    private CompoundVariable q;
    private CompoundVariable varName;
    private int mode = 1;
    private String key;
    private String iv;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        if (org.apache.commons.lang3.StringUtils.isBlank(this.key)) {
            this.key = "828d1bc65eefc6c88ca1a5d4";
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(this.iv)) {
            this.iv = "828d1bc6";
        }
        String result = "";
        try {
            result = TestUtils.des3Cipher(this.key, this.iv, mode, q.execute());
            if (varName != null) {
                TestUtils.saveVariables(varName, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 1, 3);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.q = compoundVariables[0];
        try {
            this.mode = Integer.parseInt(compoundVariables[1].execute()) == 2 ? 2 : 1;
        } catch (Exception e) {
        }
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
