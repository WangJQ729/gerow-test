package com.gerow.test.entity;

import com.gerow.test.task.ITestClass;
import com.gerow.test.task.ITestSuite;
import com.gerow.test.task.YmlTestClass;
import com.gerow.test.utils.TestUtils;
import com.gerow.test.utils.data.DataProviderUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class YmlTestClassEntity {
    /**
     * 测试名称
     */
    private String name;
    private String story;
    private String platform;
    /**
     * 参数化
     */
    private List<LinkedHashMap<String, Object>> dataProvider = new ArrayList<>();
    /**
     * 测试方法
     */
    private List<YmlTestMethodEntity> testMethod = new ArrayList<>();
    /**
     * suite前执行的测试方法
     */
    private YmlTestMethodEntity beforeSuite;
    private YmlTestMethodEntity heartbeat;
    /**
     * class前执行的测试方法
     */
    private YmlTestMethodEntity beforeClass;
    /**
     * 方法前执行的测试方法
     */
    private YmlTestMethodEntity beforeMethod;
    /**
     * suite后执行的测试方法
     */
    private YmlTestMethodEntity afterSuite;

    private YmlTestMethodEntity classHeartbeat;
    private YmlTestMethodEntity keyWord;
    /**
     * class后执行的测试方法
     */
    private YmlTestMethodEntity afterClass;
    /**
     * method后执行的测试方法
     */
    private YmlTestMethodEntity afterMethod;
    /**
     * 执行次数
     */
    private int invocationCount = 1;

    private boolean enable = true;

    /**
     * @param testSuite testSuite
     * @param file      文件对象
     * @return 测试方法
     */
    public List<ITestClass> build(ITestSuite testSuite, File file) {
        if (dataProvider.isEmpty()) {
            dataProvider = Collections.singletonList(new LinkedHashMap<>());
        }
        List<ITestClass> result = new ArrayList<>();
        for (int i = 0; i < invocationCount; i++) {
            int j = 0;
            for (LinkedHashMap<String, Object> data : dataProvider) {
                for (LinkedHashMap<String, String> newData : DataProviderUtils.dataProvider(data)) {
                    YmlTestClass testClass = buildTestClass(testSuite, file, newData, "" + i + j, i);
                    result.add(testClass);
                    j++;
                }
            }
        }
        if (beforeSuite != null)
            testSuite.addBeforeSuite(beforeSuite.build(result.get(0)).get(0));
        if (afterSuite != null)
            testSuite.addAfterSuite(afterSuite.build(result.get(0)).get(0));
        if (heartbeat != null) {
            testSuite.addHeartbeat(heartbeat.build(result.get(0)).get(0));
        }
        return result;
    }

    /**
     * @param testSuite testSuite
     * @param file      文件对象用于设置测试名称
     * @param data      参数
     * @param classNum  唯一标识
     * @return 测试方法
     */
    private YmlTestClass buildTestClass(ITestSuite testSuite, File file, Map<String, String> data, String classNum, int i) {
        Map<String, String> map = new LinkedHashMap<>(data);
        map.put("classNum", classNum);
        map.put("invocationCount", String.valueOf(i));
        YmlTestClass testClass = new YmlTestClass();
        if (StringUtils.isBlank(name)) {
            this.name = file.getName();
        }
        testClass.setFile(file);
        testClass.setName(this.name);
        testClass.setPlatform(this.platform);
        testClass.setStory(this.story);
        testClass.setEnable(enable);
        String feature = file.getParentFile().getName();
        String features = System.getProperty("features");
        if (!TestUtils.isRun(feature, features)) {
            String newFeature = file.getParentFile().getParentFile().getName();
            feature = StringUtils.equals(features, newFeature) ? newFeature : feature;
        }
        testClass.setFeature(feature);
        testClass.setTestMethods(testMethod.parallelStream().flatMap(entity -> entity.build(testClass).stream()).collect(Collectors.toList()));
        if (beforeClass != null) {
            testClass.setBeforeClass(beforeClass.build(testClass));
        }
        if (afterClass != null) {
            testClass.setAfterClass(afterClass.build(testClass));
        }
        if (classHeartbeat != null) {
            testClass.setClassHeartbeat(classHeartbeat.build(testClass));
        }
        if (keyWord != null) {
            testClass.setKeyWord(keyWord.build(testClass));
        }
        if (beforeMethod != null) {
            testClass.setBefore(beforeMethod.build(testClass));
        }
        if (afterMethod != null) {
            testClass.setAfter(afterMethod.build(testClass));
        }
        testClass.setTestSuite(testSuite);
        testClass.setParams(map);
        return testClass;
    }

}
