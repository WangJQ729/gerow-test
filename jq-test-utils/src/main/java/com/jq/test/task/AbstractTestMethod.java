package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.SeverityLevel;
import lombok.Data;

import java.util.*;

@Data
public abstract class AbstractTestMethod implements ITestMethod {
    private SeverityLevel severityLevel;
    private String name;
    private String description;
    private ITestClass testClass;
    private Map<String, String> params = new HashMap<>();
    private List<ITestStep> testSteps;
    private Map<String, String> links = new HashMap<>();

    @Override
    public void doing() {
        for (String key : new ArrayList<>(getParams().keySet())) {
            getParams().put(key, replace(getParams().get(key)));
            TestUtils.addParams(getParams());
        }
        doStep();
    }

    /**
     * 执行测试步骤
     */
    private void doStep() {
        //如果没有测试类，则不需要设置allure参数
        if (getTestClass() != null) {
            setAllure();
        }
        for (ITestStep testStep : testSteps) {
            testStep.doing();
        }
    }

    /**
     * 设置allure参数
     */
    protected void setAllure() {
        Allure.epic(getTestClass().getTestSuite().getDirPath());
        Allure.feature(getTestClass().getTestSuite().getName());
        Allure.story(getTestClass().getName());
    }

    @Override
    public String replace(String content) {
        return TestUtils.replace(content, getTestClass(), params, 0);
    }

    @Override
    public void save(String key, String value) {
        params.put(key, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParams(), getTestClass().getParams());
    }

    @Override
    public String getName() {
        return replace(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTestMethod that = (AbstractTestMethod) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(hashCode());
    }

    public String getDescription() {
        return replace(description);
    }
}
