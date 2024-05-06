package com.gerow.test.listener;

import com.gerow.test.task.ITestMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.testng.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class DingTalkListener implements ISuiteListener {
    private String platform = System.getProperty("platform");
    private String features = System.getProperty("features");
    private String story = System.getProperty("story");
    private String component = System.getProperty("component");
    private String env = System.getProperty("env");
    private String profiles = System.getProperty("spring.profiles.active");
    private String shopName = System.getProperty("shopName");

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
        StringBuilder builder = builderMessage("脚本开始运行：\n");
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
        StringBuilder builder = builderMessage("脚本运行结果：\n");
        builder.append(buildResultMessage(suite));
        builder.append(String.format("> ###### [点击查看测试报告](%s) \n", System.getProperty("build_url") + "allure/"));
        doPost(body, builder);
    }

    private StringBuilder builderMessage(String title) {
        StringBuilder builder = new StringBuilder();
        Properties properties = new Properties();
        String file = String.format("application-%s.yml", profiles);
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(file)) {
            properties.load(new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8));
            builder.append(title).append("\n");
            builder.append(String.format("> #### 平台：%s\n", platform));
            if (StringUtils.isNoneBlank(env)) {
                builder.append(String.format("> #### namespace：%s\n", env));
            } else {
                builder.append(String.format("> #### 目标测试环境：%s\n", properties.getProperty("host")));
            }
            builder.append(String.format("> #### 店铺：%s\n", StringUtils.isNoneBlank(shopName) ? shopName : properties.getProperty("shopName")));
            builder.append(String.format("> #### 功能：%s\n", features));
            if (!StringUtils.isEmpty(story)) {
                builder.append(String.format("> #### 催单类型：%s\n", story));
            }
            if (!StringUtils.isEmpty(component)) {
                builder.append(String.format("> #### 组件：%s\n", component));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    private StringBuilder buildResultMessage(ISuite suite) {
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
            builder.append(buildUseTime(tc.getEndDate().toInstant().getEpochSecond() - tc.getStartDate().toInstant().getEpochSecond()));
            if (failed > 0 && failed < 15) {
                builder.append("> ###### Failed list:\n");
                for (ITestResult allResult : failedResult) {
                    ITestMethod iTestMethod = (ITestMethod) allResult.getParameters()[0];
                    String failedName = iTestMethod.getTestClass().getStory() + "-" + iTestMethod.getName();
                    Map<String, String> links = iTestMethod.getLinks();
                    if (links.containsKey("TAPD_BUG")) {
                        builder.append(String.format("> ###### [%s](%s)", failedName, links.get("TAPD_BUG"))).append("\n");
                    } else {
                        builder.append("> ######    ").append(failedName).append("\n");
                    }
                }
            }
            if (skipped > 0 && failed < 15) {
                builder.append("> ###### Skipped list:\n");
                for (ITestResult allResult : skippedResult) {
                    String skippedName = ((ITestMethod) allResult.getParameters()[0]).getTestClass().getStory() + "-" + allResult.getName();
                    builder.append("> ######    ").append(skippedName).append("\n");
                }
            }
        }
        return builder;
    }

    private StringBuilder buildUseTime(long compare) {
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

    private void doPost(String body, StringBuilder builder) {
        if (isSendMessageTime()) {
            RestTemplate restTemplate = new RestTemplate();
            String format = String.format(body, builder.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity entity = new HttpEntity<>(format, headers);
            String ACCESS_TOKEN = "5227f6880b79746e70bd5258a69f5839c4f5a29bf33f69f99143fad9775a569c";
            String dev_url = "https://oapi.dingtalk.com/robot/send?access_token=" + ACCESS_TOKEN;
            restTemplate.postForObject(dev_url, entity, String.class);
        }
    }

    private boolean isSendMessageTime() {
        int hour = LocalTime.now().getHour();
        return hour >= 9 && hour <= 22;
    }
}
