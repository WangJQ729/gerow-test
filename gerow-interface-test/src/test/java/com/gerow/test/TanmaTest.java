package com.gerow.test;

import com.google.common.io.BaseEncoding;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TanmaTest {
    @Test
    public void test() {
        String appId = "ww9c5530210a0d5116_lFERAr";
        String appKey = "Chey5yRnVGnb7N5Kp9yA9cfU3mTtUuO4";
        String timestamp = "1627038225577";
        String body = "";
        String str = appId + timestamp + body + appKey;
        String sign = "775e156e33f58ea35674de4e9a390ff4298be768a01bb5a998c851f8a770bf9e";
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前环境不支持 SHA-256", e);
        }
        String result = BaseEncoding.base16().encode(sha256.digest(str.getBytes()));
        Assertions.assertThat(result).isEqualTo(sign);


    }
}
