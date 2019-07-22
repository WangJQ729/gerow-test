package com.jq.test.task;

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

    /**
     * 设置关联bug的url
     *
     * @param bug bug的链接
     */
    void setBug(String bug);

    /**
     * @return bug的url
     */
    String getBug();
}
