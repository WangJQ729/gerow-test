package com.gerow.test;

import org.apache.commons.codec.binary.Base32;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GoogleAuthenticatorTest {

    // 使用密钥生成验证码
    public static int getTotpCode(String secretKey) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        String secretKey = "EFSJWZNZDPM7Q6US"; // 将"您的密钥"替换为您的实际密钥
        int code = getTotpCode(secretKey);
        System.out.println("Current code: " + code);
    }
}
