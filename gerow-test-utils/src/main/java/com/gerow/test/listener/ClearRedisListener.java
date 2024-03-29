package com.gerow.test.listener;

import com.gerow.test.client.RestTemplateTool;
import com.gerow.test.utils.data.ConfigManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ClearRedisListener implements ITestListener {


    @Override
    public void onTestStart(ITestResult result) {
        RestTemplateTool.getInstance().getForObject(ConfigManager.getProperties().get("test_tools") + "/api/v1/tools/clearTaskCache", String.class);
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
