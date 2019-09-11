package com.jq.test.jmeter.functions;

import com.jq.test.utils.TestUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Collection;
import java.util.List;

public class DES3Cipher extends AbstractFunction {

    private static final String KEY = "__DES3Cipher";
    private CompoundVariable q;
    private CompoundVariable varName;
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
        byte[] key = this.key.getBytes();
        byte[] iv = this.iv.getBytes();
        String result = "";
        try {
            DESedeKeySpec spec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            result = StringUtils.toHexString(cipher.doFinal(q.execute().getBytes()));
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
        checkParameterCount(parameters, 1, 2);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.q = compoundVariables[0];
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
