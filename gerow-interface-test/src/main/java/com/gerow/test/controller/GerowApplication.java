package com.gerow.test.controller;

import com.gerow.test.TestPlatform;
import com.gerow.test.task.*;
import com.gerow.test.utils.ResponseInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GerowApplication {

    private final ITestSuite taobaoTestSuite = getTestSuite("淘宝");
    private final ITestSuite jdsopTestSuite = getTestSuite("京东SOP");
    private final ITestSuite kucoinTestSuite = getTestSuite("kucoin");
    private final ITestSuite jdzyTestSuite = getTestSuite("京东自营");
    private final ITestSuite myTestSuite = getTestSuite("测试框架");

    public GerowApplication() throws UnsupportedEncodingException {
    }

    /**
     * 获取所有关键字
     *
     * @param platform 脚本所属平台
     * @return 关键字列表
     */
    @GetMapping("/getKeyWord/{platform}")
    public ResponseInfo<List<String>> getKeyWord(@PathVariable TestPlatform platform) throws UnsupportedEncodingException {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(initTestSuite(testSuite));
    }

    /**
     * 获取关键字详细信息
     *
     * @param platform 平台名称
     * @param keyWord  关键字名称
     * @return 关键字内容
     */
    @GetMapping("/getKeyWord/{platform}/{keyWord}")
    public ResponseInfo<YmlTestStep> getKeyWords(@PathVariable TestPlatform platform, @PathVariable String keyWord) {
        ITestSuite testSuite = getTestSuite(platform);
        YmlTestStep iTestStep = (YmlTestStep) testSuite.getTestClass().stream().flatMap(testClass -> {
                    List<ITestMethod> testMethods = new ArrayList<>(testClass.getTestMethods());
                    testMethods.addAll(testClass.getBeforeClass());
                    testMethods.addAll(testClass.getAfterClass());
                    testMethods.addAll(testClass.getBefore());
                    testMethods.addAll(testClass.getAfter());
                    testMethods.addAll(testClass.getClassHeartbeat());
                    testMethods.addAll(testClass.getKeyWord());
                    testMethods.addAll(testClass.getTestSuite().getBeforeSuite());
                    testMethods.addAll(testClass.getTestSuite().getAfterSuite());
                    testMethods.addAll(testClass.getTestSuite().getHeartbeat());
                    return testMethods.stream();
                }).flatMap(method -> method.getTestSteps().stream())
                .filter(testStep -> StringUtils.equals(testStep.getName(), keyWord))
                .map(testStep -> ((YmlTestStep) testStep).getStep().build(((YmlTestStep) testStep).getTestMethod(), ((YmlTestStep) testStep).getFactory()).get(0))
                .findFirst().orElseGet(YmlTestStep::new);
        iTestStep.setStep(iTestStep.buildStep(iTestStep.getStep()));
        return ResponseInfo.success(iTestStep);
    }

    /**
     * 获取测试类列表
     *
     * @param platform 平台名称
     * @return 所有测试类名称
     */
    @GetMapping("/getTestClass/{platform}")
    public ResponseInfo<List<String>> getTestClassList(@PathVariable TestPlatform platform) {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(testSuite.getTestClass().stream()
                .map(ITestClass::getName)
                .distinct()
                .collect(Collectors.toList()));
    }

    /**
     * 根据名称获取测试类
     *
     * @param platform 平台名称
     * @param name     类名称
     * @return 类详细信息列表
     */
    @GetMapping("/getTestClass/{platform}/{name}")
    public ResponseInfo<List<ITestClass>> getTestClass(@PathVariable TestPlatform platform, @PathVariable String name) {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(name, iTestClass.getName()))
                .collect(Collectors.toList()));
    }

    /**
     * 根据名称获取测试类
     *
     * @param platform   平台名称
     * @param name       类名称
     * @param node_state 订单状态
     * @return 类详细信息列表
     */
    @GetMapping("/getTestClass/{platform}/{name}/{node_state}")
    public ResponseInfo<List<ITestClass>> getTestClass(@PathVariable TestPlatform platform, @PathVariable String name, @PathVariable String node_state) {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(name, iTestClass.getName())
                        && StringUtils.equals(iTestClass.getParams().get("node_state"), node_state))
                .collect(Collectors.toList()));
    }

    /**
     * 获取测试方法
     *
     * @param platform   平台名称
     * @param testClass  测试类名称
     * @param testMethod 测试方法名称
     * @return 测试方法列表
     */
    @GetMapping("/getTestMethod/{platform}/{testClass}/{testMethod}")
    public ResponseInfo<List<ITestMethod>> getTestMethod(@PathVariable TestPlatform platform,
                                                         @PathVariable String testClass,
                                                         @PathVariable String testMethod) {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(iTestClass.getName(), testClass))
                .flatMap(iTestClass -> {
                    List<ITestMethod> testMethods = new ArrayList<>(iTestClass.getTestMethods());
                    testMethods.addAll(iTestClass.getBeforeClass());
                    testMethods.addAll(iTestClass.getAfterClass());
                    testMethods.addAll(iTestClass.getBefore());
                    testMethods.addAll(iTestClass.getAfter());
                    testMethods.addAll(iTestClass.getClassHeartbeat());
                    testMethods.addAll(iTestClass.getKeyWord());
                    return testMethods.stream();
                })
                .filter(iTestMethod -> StringUtils.equals(iTestMethod.getName(), testMethod))
                .distinct()
                .collect(Collectors.toList()));
    }

    /**
     * 获取测试方法
     *
     * @param platform   平台名称
     * @param testClass  测试类名称
     * @param testMethod 测试方法名称
     * @param node_state 订单状态
     * @return 测试方法列表
     */
    @GetMapping("/getTestMethod/{platform}/{testClass}/{testMethod}/{node_state}")
    public ResponseInfo<List<ITestMethod>> getTestMethod(@PathVariable TestPlatform platform,
                                                         @PathVariable String testClass,
                                                         @PathVariable String testMethod,
                                                         @PathVariable String node_state) {
        ITestSuite testSuite = getTestSuite(platform);
        return ResponseInfo.success(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(iTestClass.getName(), testClass))
                .flatMap(iTestClass -> {
                    List<ITestMethod> testMethods = new ArrayList<>(iTestClass.getTestMethods());
                    testMethods.addAll(iTestClass.getBeforeClass());
                    testMethods.addAll(iTestClass.getAfterClass());
                    testMethods.addAll(iTestClass.getBefore());
                    testMethods.addAll(iTestClass.getAfter());
                    testMethods.addAll(iTestClass.getClassHeartbeat());
                    testMethods.addAll(iTestClass.getKeyWord());
                    return testMethods.stream();
                })
                .filter(iTestMethod -> StringUtils.equals(iTestMethod.getName(), testMethod))
                .distinct()
                .filter(iTestMethod ->
                        StringUtils.equals(iTestMethod.getTestClass().getParams().get("node_state"), node_state))
                .collect(Collectors.toList()));
    }

    private ITestSuite getTestSuite(TestPlatform platform) {
        ITestSuite testSuite = taobaoTestSuite;
        switch (platform) {
            case kucoin:
                testSuite = kucoinTestSuite;
                break;
            case jdsop:
                testSuite = jdsopTestSuite;
                break;
            case jdzy:
                testSuite = jdzyTestSuite;
                break;
            case test:
                testSuite = myTestSuite;
                break;
        }
        return testSuite;
    }

    private List<String> initTestSuite(ITestSuite testSuite) throws UnsupportedEncodingException {
        return testSuite.getTestClass().stream()
                .flatMap(testClass -> {
                    List<ITestMethod> testMethods = new ArrayList<>(testClass.getTestMethods());
                    testMethods.addAll(testClass.getBeforeClass());
                    testMethods.addAll(testClass.getAfterClass());
                    testMethods.addAll(testClass.getBefore());
                    testMethods.addAll(testClass.getAfter());
                    testMethods.addAll(testClass.getClassHeartbeat());
                    testMethods.addAll(testClass.getKeyWord());
                    testMethods.addAll(testClass.getTestSuite().getBeforeSuite());
                    testMethods.addAll(testClass.getTestSuite().getAfterSuite());
                    testMethods.addAll(testClass.getTestSuite().getHeartbeat());
                    return testMethods.stream();
                })
                .map(ITestMethod::getTestSteps)
                .flatMap(Collection::stream)
                .filter(iTestStep -> StringUtils.isNotBlank(iTestStep.getName()))
                .map(iTestStep ->
                        ((YmlTestStep) iTestStep).getStep().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private static ITestSuite getTestSuite(String testDir) throws UnsupportedEncodingException {
        testDir = (StringUtils.isBlank(testDir) ? "testCase" : "testCase" + "/" + testDir);
        String testDirPath = Objects.requireNonNull(GerowApplication.class.getClassLoader().getResource(testDir)).getFile();
        return YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
    }
}
