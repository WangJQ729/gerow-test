package com.gerow.test.task;

import com.gerow.test.utils.TestUtils;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;

public class YmlTestMethod extends AbstractTestMethod {
    @Override
    public boolean enable() {
        boolean isSeverityRun = true;
        String severity = System.getProperty("test.severity");
        if (StringUtils.isNotBlank(severity)) {
            if (!StringUtils.equalsIgnoreCase(severity, "ALL")) {
                isSeverityRun = StringUtils.containsIgnoreCase(severity, this.getSeverityLevel().name());
            }
        }
        return TestUtils.isRun(this.getName(), System.getProperty("test.name")) && isEnable() && isSeverityRun;
    }

    @Override
    protected void setAllure() {
        Allure.epic(getTestClass().getFile().getParentFile().getParentFile().getName());
        if (StringUtils.isNotBlank(getTestClass().getStory())) {
            Allure.feature(getTestClass().getStory());
        } else {
            Allure.feature(getTestClass().getFile().getParentFile().getName());
        }
        Allure.story(getTestClass().getName());
        for (String key : getLinks().keySet()) {
            Allure.link(key, getLinks().get(key));
        }
        Allure.descriptionHtml(getDescription() + "</br>Author: " + getAuthor());
    }
}
