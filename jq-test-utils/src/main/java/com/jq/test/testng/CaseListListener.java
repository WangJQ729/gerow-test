package com.jq.test.testng;

import com.alibaba.fastjson.JSONObject;
import com.jq.test.task.ITestMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class CaseListListener implements ITestListener {


    private static String token;

    @Override
    public void onTestStart(ITestResult result) {
        ITestMethod testMethod = (ITestMethod) (result.getParameters()[0]);
        String suite = testMethod.getTestClass().getTestSuite().getName();
        String feature = testMethod.getTestClass().getFeature();
        String name = testMethod.getName();
        String severityLevel = testMethod.getSeverityLevel().value();
        String description = testMethod.getDescription();
        String creator = testMethod.getAuthor();
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCase_name(suite + "-" + feature + "-" + name);
        caseInfo.setDescription(description);
        caseInfo.setFeature(feature);
        caseInfo.setCreator(creator);
        caseInfo.setStory(suite);
        caseInfo.setSeverity(severityLevel);
        doPost(caseInfo);
    }

    private void doPost(CaseInfo caseInfo) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String host = "http://test-tools.xiaoduoai.com";
        if (StringUtils.isEmpty(token)) {
            HttpEntity entity = new HttpEntity<>("{\"username\":\"wangjianqiang\",\"password\":\"wangjianqiang123456\"}\n", headers);
            String login = "/test1/v1/login/";
            JSONObject loginResult = restTemplate.postForObject(host + login, entity, JSONObject.class);
            assert loginResult != null;
            token = loginResult.getJSONObject("data").getString("token");
        }
        headers.set(HttpHeaders.AUTHORIZATION, "auth " + token);
        HttpEntity entity = new HttpEntity<>(caseInfo, headers);
        String caseCreate = "/test1/v1/auto_test/caseCreate";
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
