package com.gerow.test.jmeter.functions;

import com.gerow.test.utils.TestUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

public class HmacMD5 extends AbstractFunction {

    private static final String KEY = "__HmacMD5";
    private static final String key = "6xHtKkcEfGAB2NaEM9p89Tqrj2SeCTbh";
    private CompoundVariable vars;
    private CompoundVariable varName;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        String src = vars.execute().trim();
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacMD5");
        String result = "";
        try {
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(src.getBytes(StandardCharsets.UTF_8));
            result = Hex.encodeHexString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        if (varName != null && StringUtils.isNotBlank(varName.execute())) {
            TestUtils.saveVariables(varName, result);
        }
        return result;
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.vars = compoundVariables[0];
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
