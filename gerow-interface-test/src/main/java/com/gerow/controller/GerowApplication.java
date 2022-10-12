package com.gerow.controller;

import com.gerow.test.task.ITestClass;
import com.gerow.test.task.ITestSuite;
import com.gerow.test.task.YmlTestSuite;
import net.minidev.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class GerowApplication {

    @GetMapping("/allTest")
    public Object getAllTest(String testDir) throws UnsupportedEncodingException {
        testDir = StringUtils.isBlank(testDir) ? System.getProperty("platform") : testDir;
        testDir = StringUtils.isBlank(testDir) ? "testCase" : "testCase" + "/" + testDir;
        String testDirPath = Objects.requireNonNull(GerowApplication.class.getClassLoader().getResource(testDir)).getFile();
        ITestSuite testSuite = YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
        System.out.println(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(iTestClass.getFeature(), System.getProperty("features")))
                .map(ITestClass::getStory)
                .distinct()
                .collect(Collectors.joining(",")));
        System.out.println(testSuite.getTestClass().stream()
                .filter(iTestClass -> StringUtils.equals(iTestClass.getFeature(), System.getProperty("features")))
                .map(ITestClass::getName)
                .distinct()
                .collect(Collectors.joining(",")));
        return testSuite.getTestClass().parallelStream()
                .filter(ITestClass::enable)
                .toArray();
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        String testDir = "testCase/kucoin";
        String testDirPath = Objects.requireNonNull(GerowApplication.class.getClassLoader().getResource(testDir)).getFile();
        ITestSuite testSuite = YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
        Object[] objects = testSuite.getTestClass().parallelStream()
                .filter(ITestClass::enable)
                .toArray();
        for (Object object : objects) {
            System.out.println(object.toString());
        }
    }
}
