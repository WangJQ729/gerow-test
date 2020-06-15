package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.SeverityLevel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class AbstractTestMethod implements ITestMethod {
    private SeverityLevel severityLevel;
    private String name;
    private String description;
    private String author;
    private ITestClass testClass;
    private Map<String, String> params = new HashMap<>();
    private List<ITestStep> testSteps;
    private Map<String, String> links = new HashMap<>();
    private boolean enable;

    @Override
    public void doing() {
        for (String key : new ArrayList<>(getParams().keySet())) {
            getParams().put(key, replace(getParams().get(key)));
            TestUtils.addParams(this);
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
        return TestUtils.replace(content, getTestClass(), this, 0);
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
        if (StringUtils.isNoneBlank(description)) {
            return replace(description);
        }
        return "\"\"\"</br>"+getTestSteps().stream().map(iTestStep -> {
            if (StringUtils.isNoneBlank(iTestStep.getName())) {
                return iTestStep.getName();
            } else {
                return iTestStep.getByName();
            }
        }).collect(Collectors.joining("</br>"))+"</br>\"\"\"</br>";
    }
}
