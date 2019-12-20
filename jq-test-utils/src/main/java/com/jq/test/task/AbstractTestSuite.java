package com.jq.test.task;

import com.jq.test.utils.ConfigManager;
import com.jq.test.utils.TestUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class AbstractTestSuite implements ITestSuite {
    private File file;
    private List<ITestClass> testClass = new ArrayList<>();
    private Map<String, String> params = new HashMap<>();
    private List<ITestMethod> beforeSuite = new ArrayList<>();
    private List<ITestMethod> afterSuite = new ArrayList<>();

    @Override
    public void addBeforeSuite(ITestMethod testMethod) {
        this.beforeSuite.add(testMethod);
    }

    @Override
    public void addAfterSuite(ITestMethod testMethod) {
        this.afterSuite.add(testMethod);
    }

    @Override
    public synchronized void setUp() {
        this.params.putAll(ConfigManager.getProperties());
        //只运行features下的前置条件
        beforeSuite.stream().filter(iTestMethod ->
                TestUtils.isRun(iTestMethod.getTestClass().getFeature(), System.getProperty("features")))
                .forEach(ITestMethod::doing);
    }

    @Override
    public synchronized void tearDown() {
        //只运行features下的前置条件
        afterSuite.stream().filter(iTestMethod ->
                TestUtils.isRun(iTestMethod.getTestClass().getFeature(), System.getProperty("features")))
                .forEach(ITestMethod::doing);
    }

    @Override
    public void save(String key, String value) {
        params.put(key, value);
    }

    @Override
    public String replace(String content) {
        return TestUtils.replace(content, this);
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    private boolean isTearDownRun = false;

    @Override
    public String getDirPath() {
        return getPath(file, "");
    }

    private String getPath(File file, String name) {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            String newName = StringUtils.isBlank(name) ? parentFile.getName() : parentFile.getName() + "/" + name;
            if (StringUtils.equals(parentFile.getName(), "testCase")) {
                return newName;
            } else {
                return getPath(parentFile, newName);
            }
        } else {
            return name;
        }
    }
}
