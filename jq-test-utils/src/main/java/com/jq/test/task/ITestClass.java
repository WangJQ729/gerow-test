package com.jq.test.task;

import org.apache.jmeter.threads.JMeterVariables;

import java.io.File;
import java.util.List;

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
     * 执行beforeClass
     */
    void setUpBeforeClass();

    String getPlatform();

    void setPlatform(String platform);

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

    List<ITestMethod> getClassHeartbeat();

    List<ITestMethod> getKeyWord();

    File getFile();

    String getStory();

    void setStory(String story);

    void setFile(File file);

    String getFeature();

    void setFeature(String feature);

    boolean isEnable();

    void setEnable(boolean enable);
}
