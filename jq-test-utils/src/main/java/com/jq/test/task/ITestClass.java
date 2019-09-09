package com.jq.test.task;

import org.apache.jmeter.threads.JMeterVariables;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ITestClass extends ITest {
    /**
     * @return 所有测试方法
     */
    List<ITestMethod> getTestMethods();

    /**
     * @return 测试套件
     */
    ITestSuite getTestSuite();

    /**
     * @return 所有参数
     */
    Map<String, String> getParams();

    /**
     * 执行beforeClass
     */
    void setUpBeforeClass();

    /**
     * 添加参数
     *
     * @param jMeterVariables 参数内容
     */
    void addParams(JMeterVariables jMeterVariables);

    /**
     * 执行AfterClass
     */
    void tearDownAfterClass();

    /**
     * 执行AfterMethod
     */
    void tearDown();

    /**
     * 执行BeforeMethod
     */
    void setUp();

    /**
     * @return beforeClass
     */
    List<ITestMethod> getBeforeClass();

    /**
     * @return afterClass
     */
    List<ITestMethod> getAfterClass();

    /**
     * @return before
     */
    List<ITestMethod> getBefore();

    /**
     * @return after
     */
    List<ITestMethod> getAfter();

    File getFile();

    String getFeature();

    void setFeature(String feature);

    void setFile(File file);
}
