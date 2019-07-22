package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;

public class YmlTestMethod extends AbstractTestMethod {
    @Override
    public boolean enable() {
        return TestUtils.isRun(this.getName(), System.getProperty("test.name"));
    }

    @Override
    protected void setAllure() {
        Allure.epic(getTestClass().getTestSuite().getFile().getName());
        Allure.feature(getTestClass().getTestSuite().getName());
        Allure.story(getTestClass().getName());
        if (StringUtils.isNotBlank(getBug())) {
            Allure.link("关联TAPD缺陷", getBug());
        }
        if (StringUtils.isNotBlank(getStories())) {
            Allure.link("关联TAPD需求", getStories());
        }
    }
}
