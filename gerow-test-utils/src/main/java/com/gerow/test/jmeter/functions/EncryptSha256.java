package com.gerow.test.jmeter.functions;

import com.google.common.io.BaseEncoding;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

public class EncryptSha256 extends AbstractFunction {

    private CompoundVariable vars;

    private static final String KEY = "__REncryptSha256";


    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) {
        String value = vars.execute();
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前环境不支持 SHA-256", e);
        }
        String result = BaseEncoding.base16().lowerCase().encode(sha256.digest(value.getBytes()));
        System.out.println(result);
        return result;
    }

    @Test
    public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String appId = "wwd074f3968f56dcb0_MUVXzH";
        String appKey = "OtfNkhTNnGC45aRL37y1m50R17dba9wG";
        String timestamp = "1679210962208";
        String body = "{\"customerName\":\"【pf】向子(1679210962)\",\"mobiles\":[\"86167921096220899\"],\"fields\":[]}";
        String str = appId + timestamp + body + appKey;

        str = "wwd074f3968f56dcb0_MUVXzH1679212304460{\"customerName\":\"【pf】_叶彩(1679212304)\",\"mobiles\":[\"86167921230446042\"],\"fields\":[]}OtfNkhTNnGC45aRL37y1m50R17dba9wG";
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前环境不支持 SHA-256", e);
        }
        String result = BaseEncoding.base16().lowerCase().encode(sha256.digest(str.getBytes()));
        Assertions.assertThat(result).isEqualTo("728ee46609cf81c737834d1bc88e1fd95ee85852e81a64b298a973539cb9596a");


    }


    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 0);
        CompoundVariable[] compoundVariables = parameters.toArray(new CompoundVariable[0]);
        if (compoundVariables.length > 0) {
            vars = compoundVariables[0];
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
