package com.gerow.controller;

import com.gerow.enums.TestSuiteEnum;
import com.gerow.test.task.ITestMethod;
import com.gerow.test.task.ITestSuite;
import com.gerow.test.task.YmlTestStep;
import com.gerow.test.task.YmlTestSuite;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class GerowApplication {

    private final ITestSuite taobaoTestSuite = getTestSuite("淘宝");
    private final ITestSuite jdsopTestSuite = getTestSuite("京东SOP");
    private final ITestSuite kucoinTestSuite = getTestSuite("kucoin");
    ;

    private final String kucoin;
    private final String jdsop;
    private final String taobao;

    public GerowApplication() throws UnsupportedEncodingException {
        kucoin = initTestSuite("kucoin");
        jdsop = initTestSuite("京东SOP");
        taobao = initTestSuite("淘宝");
    }

    @GetMapping("/getKeyWord/{enums}")
    public String getKeyWord(@PathVariable TestSuiteEnum enums) {
        switch (enums) {
            case taobao:
                return taobao;
            case kucoin:
                return kucoin;
            case jdsop:
                return jdsop;
        }
        return kucoin;
    }

    @GetMapping("/getKeyWord/{enums}/{keyWord}")
    public Object getKeyWordsInfo(@PathVariable TestSuiteEnum enums, @PathVariable String keyWord) {
        ITestSuite testSuite = taobaoTestSuite;
        switch (enums) {
            case kucoin:
                testSuite = kucoinTestSuite;
                break;
            case jdsop:
                testSuite = jdsopTestSuite;
                break;
        }
        return testSuite.getTestClass().stream().flatMap(testClass -> {
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
                .findFirst().get();

    }

    private String initTestSuite(String testDir) throws UnsupportedEncodingException {
        ITestSuite testSuite = getTestSuite(testDir);
        return JSONArray.toJSONString(testSuite.getTestClass().stream()
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
                .collect(Collectors.toList()));
    }

    private static ITestSuite getTestSuite(String testDir) throws UnsupportedEncodingException {
        testDir = (StringUtils.isBlank(testDir) ? "testCase" : "testCase" + "/" + testDir);
        String testDirPath = Objects.requireNonNull(GerowApplication.class.getClassLoader().getResource(testDir)).getFile();
        return YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
    }
}
