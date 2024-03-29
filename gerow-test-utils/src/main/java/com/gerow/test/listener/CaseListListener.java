package com.gerow.test.listener;

import com.alibaba.fastjson.JSONObject;
import com.gerow.test.task.ITestMethod;
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
        String host = "https://tester.xiaoduoai.com/";
        HttpEntity entity = new HttpEntity<>(caseInfo, headers);
        String caseCreate = "/api/v1/auto_test/caseCreate";
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
