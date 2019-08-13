package com.jq.test.task;

import io.qameta.allure.SeverityLevel;

import java.util.List;
import java.util.Map;

public interface ITestMethod extends ITest {

    /**
     * 执行测试方法
     */
    void doing();

    /**
     * @return 测试类
     */
    ITestClass getTestClass();

    /**
     * @return 参数
     */
    Map<String, String> getParams();

    /**
     * 设置ITestClass
     *
     * @param testClass 测试类
     */
    void setTestClass(ITestClass testClass);

    /**
     * @return 所有测试步骤
     */
    List<ITestStep> getTestSteps();


    Map<String, String> getLinks();

    void setLinks(Map<String, String> links);

    SeverityLevel getSeverityLevel();
}
