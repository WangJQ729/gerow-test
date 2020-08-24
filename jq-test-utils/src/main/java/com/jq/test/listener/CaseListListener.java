package com.jq.test.listener;

import com.alibaba.fastjson.JSONObject;
import com.jq.test.task.ITestMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CaseListListener implements ITestListener {


    private static String token;
    private final static Log logger = LogFactory.getLog(CaseListListener.class);
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onTestStart(ITestResult result) {
        addTestCase(result);
    }

    private void addTestCase(ITestResult result) {
        ITestMethod testMethod = (ITestMethod) (result.getParameters()[0]);
        String platform = System.getProperty("platform");
        String feature = System.getProperty("features");
        String story = testMethod.getTestClass().getStory();
        String name = testMethod.getName();
        String severityLevel = testMethod.getSeverityLevel().value();
        String description = testMethod.getDescription();
        String creator = testMethod.getAuthor();
        CaseInfo caseInfo = new CaseInfo();
        try {
            caseInfo.setHost(InetAddress.getLocalHost().getHostAddress());
            logger.info("-----------------------------------------------------------");
            logger.info("IP--" + InetAddress.getLocalHost().getHostAddress());
            logger.info("-----------------------------------------------------------");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        caseInfo.setCase_name(platform + feature + "-" + story + "-" + name);
        caseInfo.setFeature(platform + feature);
        caseInfo.setPlatform(platform);
        caseInfo.setStory(story);
        caseInfo.setDescription(description);
        caseInfo.setCreator(creator);
        caseInfo.setSeverity(severityLevel);
        try {
            doPost(caseInfo);
        } catch (Exception e) {
            logger.warn("添加case失败！");
        }
    }


    private void doPost(CaseInfo caseInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String host = "http://test-tools.xiaoduoai.com";
        if (StringUtils.isEmpty(token)) {
            HttpEntity entity = new HttpEntity<>("{\"username\":\"wangjianqiang\",\"password\":\"wangjianqiang123456\"}\n", headers);
            String login = "/api/api/v1/login/";
            JSONObject loginResult = restTemplate.postForObject(host + login, entity, JSONObject.class);
            assert loginResult != null;
            token = loginResult.getJSONObject("data").getString("token");
        }
        headers.set(HttpHeaders.AUTHORIZATION, "auth " + token);
        HttpEntity entity = new HttpEntity<>(caseInfo, headers);
        String caseCreate = "/api/api/v1/auto_test/caseCreate";
        restTemplate.postForObject(host + caseCreate, entity, JSONObject.class);
    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {

    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
