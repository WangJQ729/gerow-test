package com.jq.test.testng;

import com.jq.test.task.ITestMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DingTalkListener implements ISuiteListener {
    private String url = "https://oapi.dingtalk.com/robot/send?access_token=aa9a76956e0bcf39e7d5ddccc5437fc9afd11e81fecb41569d4b5415dd95c8e9";
    private String platform = System.getProperty("platform");
    private String features = System.getProperty("features");
    private String test_feature = System.getProperty("test.feature");

    @Override
    public void onStart(ISuite suite) {
        String body = "{\n" +
                "     \"msgtype\": \"markdown\",\n" +
                "     \"markdown\": {\n" +
                "         \"title\":\"脚本开始运行\",\n" +
                "         \"text\": \"%s\"\n" +
                "     },\n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                " }";
        StringBuilder builder = new StringBuilder();
        builder.append("开始运行自动化测试：\n");
        builder.append(String.format("> #### 平台：%s\n", platform));
        builder.append(String.format("> #### 功能：%s\n", features));
        if (!StringUtils.isEmpty(test_feature)) {
            builder.append(String.format("> #### 节点：%s\n", test_feature));
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.append("> ###### 开始时间： ").append(df.format(new Date())).append("\n");
        doPost(body, builder);

    }

    @Override
    public void onFinish(ISuite suite) {
        String body = "{\n" +
                "     \"msgtype\": \"markdown\",\n" +
                "     \"markdown\": {\n" +
                "         \"title\":\"脚本测试结果\",\n" +
                "         \"text\": \"%s\"\n" +
                "     },\n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                " }";
        StringBuilder builder = new StringBuilder();
        builder.append("测试结果：\n");
        builder.append(String.format("> #### 平台：%s\n", platform));
        builder.append(String.format("> #### 功能：%s\n", features));
        if (!StringUtils.isEmpty(test_feature)) {
            builder.append(String.format("> #### 节点：%s\n", test_feature));
        }
        Map<String, ISuiteResult> suiteResults = suite.getResults();
        for (ISuiteResult result : suiteResults.values()) {
            ITestContext tc = result.getTestContext();
            int pass = tc.getPassedTests().getAllResults().size();
            builder.append("> ###### total Passed：").append(pass).append("\n");
            int failed = tc.getFailedTests().getAllResults().size();
            builder.append("> ###### total Failed：").append(failed).append("\n");
            int skipped = tc.getSkippedTests().getAllResults().size();
            builder.append("> ###### total Skipped：").append(skipped).append("\n");
            builder.append("> ###### 失败case列表：\n");
            for (ITestResult allResult : tc.getFailedTests().getAllResults()) {
                String failedList = ((ITestMethod) allResult.getParameters()[0]).getTestClass().getFeature() + "-" + allResult.getTestName();
                builder.append("> ######    ").append(failedList).append("\n");
            }
        }
        if (StringUtils.equals(platform, "拼多多")) {
            builder.append("> ###### [点击查看测试报告](http://10.0.0.152:8080/job/java-interface-test-pdd/allure/) \n");
        } else if (StringUtils.equals(platform, "京东SOP")) {
            builder.append("> ###### [点击查看测试报告](http://10.0.0.152:8080/job/java-interface-test-jdsop/allure/) \n");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        doPost(body, builder);
    }

    private void doPost(String body, StringBuilder builder) {
        RestTemplate restTemplate = new RestTemplate();
        String format = String.format(body, builder.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity entity = new HttpEntity<>(format, headers);
        String result = restTemplate.postForObject(url, entity, String.class);
    }
}
