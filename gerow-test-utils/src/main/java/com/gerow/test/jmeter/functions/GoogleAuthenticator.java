package com.gerow.test.jmeter.functions;

import org.apache.commons.codec.binary.Base32;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.List;

public class GoogleAuthenticator extends AbstractFunction {


    private static final String KEY = "__GoogleAuthenticator";
    private CompoundVariable secretKey;

    // 使用密钥生成验证码
    private int getTotpCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        long time = (System.currentTimeMillis() / 1000) / 30;
        byte[] data = new byte[8];
        for (int i = 8; i-- > 0; time >>= 8) {
            data[i] = (byte) time;
        }

        SecretKeySpec signKey = new SecretKeySpec(bytes, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;
            return (int) truncatedHash;
        } catch (Exception ignored) {
            return -1;
        }
    }

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        return String.valueOf(getTotpCode(secretKey.execute()));
    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        this.secretKey = compoundVariables[0];
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
