package com.jq.test.task;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ITestSuite extends ITest {
    /**
     * @return 所有测试类
     */
    List<ITestClass> getTestClass();

    /**
     * 测试套件前执行
     */
    void setUp();

    /**
     * 测试套件后执行
     */
    void tearDown();

    /**
     * @return 文件目录
     */
    String getDirPath();

    /**
     * @return 所有参数
     */
    Map<String, String> getParams();

    /**
     * 添加测试套件执行前的方法
     *
     * @param testMethod 测试方法
     */
    void addBeforeSuite(ITestMethod testMethod);

    /**
     * 添加测试套件执行后的方法
     *
     * @param testMethod 测试方法
     */
    void addAfterSuite(ITestMethod testMethod);

    /**
     * @return 测试套件执行前的方法
     */
    List<ITestMethod> getBeforeSuite();

    /**
     * @return 测试套件执行后的方法
     */
    List<ITestMethod> getAfterSuite();

    /**
     * @return 测试套件文件
     */
    File getFile();
}
