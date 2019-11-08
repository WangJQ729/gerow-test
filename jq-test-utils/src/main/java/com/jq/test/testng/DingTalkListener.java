package com.jq.test.testng;

import com.jq.test.task.ITestMethod;
import com.jq.test.utils.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class DingTalkListener implements ISuiteListener {
    private String platform = System.getProperty("platform");
    private String features = System.getProperty("features");
    private String story = System.getProperty("story");
    private String component = System.getProperty("component");

    @Override
    public void onStart(ISuite suite) {
        String body = "{\n" +
                "     \"msgtype\": \"markdown\",\n" +
                "     \"markdown\": {\n" +
                "         \"title\":\"催单脚本通知\",\n" +
                "         \"text\": \"%s\"\n" +
                "     },\n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                " }";
        StringBuilder builder = builderMessageBody("脚本开始运行：\n");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.append("> ###### 开始时间： ").append(df.format(new Date())).append("\n");
        doPost(body, builder);
    }

    @Override
    public void onFinish(ISuite suite) {
        String body = "{\n" +
                "     \"msgtype\": \"markdown\",\n" +
                "     \"markdown\": {\n" +
                "         \"title\":\"催单脚本通知\",\n" +
                "         \"text\": \"%s\"\n" +
                "     },\n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                " }";
        StringBuilder builder = builderMessageBody("脚本运行结果：\n");
        builder.append(buildMessageResult(suite));
        builder.append(String.format("> ###### [点击查看测试报告](%s) \n", ConfigManager.getProperties().get("jenkins_url")));
        doPost(body, builder);
    }

    private StringBuilder buildMessageResult(ISuite suite) {
        StringBuilder builder = new StringBuilder();
        Map<String, ISuiteResult> suiteResults = suite.getResults();
        for (ISuiteResult result : suiteResults.values()) {
            ITestContext tc = result.getTestContext();
            int pass = tc.getPassedTests().getAllResults().size();
            builder.append("> ###### total Passed: ").append(pass).append("\n");
            Set<ITestResult> failedResult = tc.getFailedTests().getAllResults();
            int failed = failedResult.size();
            builder.append("> ###### total Failed: ").append(failed).append("\n");
            Set<ITestResult> skippedResult = tc.getSkippedTests().getAllResults();
            int skipped = skippedResult.size();
            builder.append("> ###### total Skipped: ").append(skipped).append("\n");
            builder.append(buildTimeCost(tc.getEndDate().toInstant().getEpochSecond() - tc.getStartDate().toInstant().getEpochSecond()));
            if (failed > 0) {
                builder.append("> ###### Failed list:\n");
                for (ITestResult allResult : failedResult) {
                    String failedName = ((ITestMethod) allResult.getParameters()[0]).getTestClass().getStory() + "-" + allResult.getName();
                    builder.append("> ######    ").append(failedName).append("\n");
                }
            }
            if (skipped > 0) {
                builder.append("> ###### Skipped list:\n");
                for (ITestResult allResult : skippedResult) {
                    String skippedName = ((ITestMethod) allResult.getParameters()[0]).getTestClass().getStory() + "-" + allResult.getName();
                    builder.append("> ######    ").append(skippedName).append("\n");
                }
            }
        }
        return builder;
    }

    private StringBuilder buildTimeCost(long compare) {
        StringBuilder builder = new StringBuilder();
        builder.append("> ###### 耗时: ");
        if (compare / 60 > 60) {
            builder.append(compare / 60 / 60).append("小时");
            builder.append(compare / 60 % 60).append("分");
        } else {
            builder.append(compare / 60).append("分");
        }
        builder.append(compare % 60).append("秒").append("\n");
        return builder;
    }

    private StringBuilder builderMessageBody(String title) {
        StringBuilder builder = new StringBuilder();
        builder.append(title).append("\n");
        String host = ConfigManager.getProperties().get("host");
        String shopName = ConfigManager.getProperties().get("shopName");
        builder.append(String.format("> #### 目标环境：%s\n", host));
        builder.append(String.format("> #### 店铺：%s\n", shopName));
        builder.append(String.format("> #### 平台：%s\n", platform));
        builder.append(String.format("> #### 功能：%s\n", features));
        if (!StringUtils.isEmpty(story)) {
            builder.append(String.format("> #### 催单类型：%s\n", story));
        }
        if (!StringUtils.isEmpty(component)) {
            builder.append(String.format("> #### 组件：%s\n", component));
        }
        return builder;
    }

    private void doPost(String body, StringBuilder builder) {
        RestTemplate restTemplate = new RestTemplate();
        String format = String.format(body, builder.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity entity = new HttpEntity<>(format, headers);
        String test_url = "https://oapi.dingtalk.com/robot/send?access_token=aa9a76956e0bcf39e7d5ddccc5437fc9afd11e81fecb41569d4b5415dd95c8e9";
        restTemplate.postForObject(test_url, entity, String.class);
        String dev_url = "https://oapi.dingtalk.com/robot/send?access_token=5227f6880b79746e70bd5258a69f5839c4f5a29bf33f69f99143fad9775a569c";
        restTemplate.postForObject(dev_url, entity, String.class);
    }
}
