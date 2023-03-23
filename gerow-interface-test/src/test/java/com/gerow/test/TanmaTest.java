package com.gerow.test;

import com.google.common.io.BaseEncoding;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TanmaTest {
    @Test
    public void test() {
        String appId = "wwd074f3968f56dcb0_MUVXzH";
        String appKey = "OtfNkhTNnGC45aRL37y1m50R17dba9wG";
        String timestamp = "1679461630682";
        String body = "{\"mobiles\":[\"86167946163068051\"],\"fields\":[],\"customerName\":\"【pf】葛进(1679461630)\"}";
        String str = appId + timestamp + body + appKey;
        String sign = "549cd342164223a2f39d9a7e91c844bcbc2a8828bee25f1fd28f88720455251c";
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前环境不支持 SHA-256", e);
        }
        byte[] digest = sha256.digest(str.getBytes(StandardCharsets.UTF_8));
        String result = BaseEncoding.base16().lowerCase().encode(digest);
        Assertions.assertThat(result).isEqualTo(sign);
    }
}
