package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import io.qameta.allure.SeverityLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class YmlTestClass extends AbstractTestClass {
    @Override
    public boolean enable() {
        if (!isEnable()) {
            return false;
        }
        boolean platform = TestUtils.isRun(System.getProperty("platform"), this.getPlatform());
        if (!platform) {
            return false;
        }
        boolean features = TestUtils.isRun(this.getFeature(), System.getProperty("features"));
        if (!features) {
            return false;
        }
        boolean story = TestUtils.isRun(this.getStory(), System.getProperty("story"));
        if (!story) {
            return false;
        }

        boolean storySkip = TestUtils.isSkip(this.getName(), System.getProperty("skipStory"));
        if (storySkip) {
            return false;
        }

        boolean component = TestUtils.isRun(this.getName(), System.getProperty("component"));
        if (!component) {
            return false;
        }
        boolean isSeverityRun = true;
        String severity = System.getProperty("test.severity");
        if (StringUtils.isNotBlank(severity)) {
            List<SeverityLevel> collect = this.getTestMethods().stream().map(ITestMethod::getSeverityLevel).collect(Collectors.toList());
            if (!StringUtils.equalsIgnoreCase(severity, "ALL")) {
                isSeverityRun = StringUtils.containsIgnoreCase(collect.toString(), severity);
            }
        }
        return !TestUtils.isSkip(this.getName(), System.getProperty("skipComponent")) && isSeverityRun;
    }
}