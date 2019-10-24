package com.jq.test.testng;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.jq.test.task.ITestMethod;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DingTalkListener implements ISuiteListener {
    private DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=aa9a76956e0bcf39e7d5ddccc5437fc9afd11e81fecb41569d4b5415dd95c8e9");
    private String platform = System.getProperty("platform");
    private String features = System.getProperty("features");
    private String test_feature = System.getProperty("test.feature");

    @Override
    public void onStart(ISuite suite) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("脚本开始运行");
        StringBuilder builder = new StringBuilder();
        builder.append("开始运行自动化测试：\n");
        builder.append(String.format(">#### 平台：%s\n", platform));
        builder.append(String.format(">#### 功能：%s\n", features));
        if (!StringUtils.isEmpty(test_feature)) {
            builder.append(String.format(">#### 节点：%s\n", test_feature));
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.append(">###### 开始时间： ").append(df.format(new Date())).append("\n");
        markdown.setText(builder.toString());
        request.setMarkdown(markdown);
        try {
            OapiRobotSendResponse response = client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("脚本测试结果");
        StringBuilder builder = new StringBuilder();
        builder.append("测试结果：\n");
        builder.append(String.format(">#### 平台：%s\n", platform));
        builder.append(String.format(">#### 功能：%s\n", features));
        if (!StringUtils.isEmpty(test_feature)) {
            builder.append(String.format(">#### 节点：%s\n", test_feature));
        }
        Map<String, ISuiteResult> suiteResults = suite.getResults();
        for (ISuiteResult result : suiteResults.values()) {
            ITestContext tc = result.getTestContext();
            int pass = tc.getPassedTests().getAllResults().size();
            builder.append(">###### total Passed：").append(pass).append("\n");
            int failed = tc.getFailedTests().getAllResults().size();
            builder.append(">###### total Failed：").append(failed).append("\n");
            int skipped = tc.getSkippedTests().getAllResults().size();
            builder.append(">###### total Skipped：").append(skipped).append("\n");
            builder.append(">###### 失败case列表：\n").append(skipped);
            for (ITestResult allResult : tc.getFailedTests().getAllResults()) {
                String failedList = ((ITestMethod) allResult.getParameters()[0]).getTestClass().getFeature() + "-" + allResult.getTestName();
                builder.append(">######    ").append(failedList).append("\n");
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.append(">###### 结束时间： ").append(df.format(new Date())).append("\n");
        markdown.setText(builder.toString());
        request.setMarkdown(markdown);
        try {
            OapiRobotSendResponse response = client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
