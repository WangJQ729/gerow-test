package com.jq.test.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigManager {

    private static Config config;
    private static ClientInfo clientInfo;

    @Autowired
    public void setConfig(Config config) {
        ConfigManager.config = config;
    }

    @Autowired
    public void setHeaders(ClientInfo headers) {
        ConfigManager.clientInfo = headers;
    }

    /**
     * @return 默认请求头
     */
    public static Map<String, String> getCustomizeHeader() {
        return clientInfo.getHeaders();
    }

    /**
     * @return http协议
     */
    public static String getProtocol() {
        return clientInfo.getProtocol();
    }

    /**
     * @return 默认地址
     */
    public static String getUrl() {
        return clientInfo.getProtocol() + "://" + config.getTest().get("baseURI");
    }

    /**
     * @return 超时时间
     */
    public static int getTimeOut() {
        return clientInfo.getTimeOut();
    }

    /**
     * @return 账号
     */
    public static String getAccount() {
        return config.getTest().get("account");
    }


    /**
     * @return 密码
     */
    public static String getPassword() {
        return config.getTest().get("password");
    }

    /**
     * @return 参数
     */
    public static Map<String, String> getProperties() {
        return config.getTest();
    }


    /**
     * data下的参数
     */
    @Configuration
    @Data
    @ConfigurationProperties(prefix = "data")
    private class Config {
        private Map<String, String> test = new HashMap<>();
    }

    /**
     * httpclient的属性
     */
    @Configuration
    @Data
    @ConfigurationProperties(prefix = "client")
    private class ClientInfo {
        private Map<String, String> headers = new HashMap<>();
        private String protocol = "";
        private int timeOut = 5;
    }
}
