package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import io.qameta.allure.Allure;

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
        for (String key : getLinks().keySet()) {
            Allure.link(key, getLinks().get(key));
        }
    }
}
