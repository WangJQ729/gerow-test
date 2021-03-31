package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public abstract class AbstractTestClass implements ITestClass {
    private String name;
    private String story;
    private String platform;
    private String feature;
    private List<ITestMethod> testMethods = new ArrayList<>();
    public Map<String, String> params = new LinkedHashMap<>();
    private ITestSuite testSuite;
    private File file;
    private boolean enable;
    private List<ITestMethod> beforeClass = new ArrayList<>();
    private List<ITestMethod> afterClass = new ArrayList<>();
    private List<ITestMethod> classHeartbeat = new ArrayList<>();
    private List<ITestMethod> keyWord = new ArrayList<>();
    private List<ITestMethod> before = new ArrayList<>();
    private List<ITestMethod> after = new ArrayList<>();


    @Override
    public void setUp() {
        for (ITestMethod testMethod : before) {
            testMethod.doing();
        }
    }

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @Override
    public synchronized void setUpBeforeClass() {
        //执行测试前，先生成参数
        LinkedHashMap<String, String> map = new LinkedHashMap<>(params);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JMeterContextService.getContext().setVariables(new JMeterVariables());
            params.put(entry.getKey(), new CompoundVariable(replace(entry.getValue())).execute());
            addParams(JMeterContextService.getContext().getVariables());
        }
        executor.submit((Runnable) () -> {
            while (true) {
                try {
                    classHeartbeat.get(0).doing();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        for (ITestMethod testMethod : beforeClass) {
            testMethod.doing();
        }
    }

    @Override
    public void tearDown() {
        for (ITestMethod testMethod : after) {
            testMethod.doing();
        }
    }

    @Override
    public void tearDownAfterClass() {
        for (ITestMethod testMethod : afterClass) {
            testMethod.doing();
        }
        executor.shutdown();
    }

    @Override
    public void addParams(JMeterVariables jMeterVariables) {
        if (jMeterVariables != null) {
            Iterator<Map.Entry<String, Object>> iterator = jMeterVariables.getIterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> map = iterator.next();
                getParams().put(map.getKey(), map.getValue().toString());
            }
        }
    }

    @Override
    public void save(String key, String value) {
        params.put(key, value);
    }

    @Override
    public String replace(String content) {
        return TestUtils.replace(content, getTestSuite(), this, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTestClass that = (AbstractTestClass) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, testMethods, params, testSuite.getParams(), beforeClass, afterClass);
    }

    @Override
    public String toString() {
        return replace(name);
    }

    @Override
    public String getName() {
        return replace(name);
    }


    @Override
    public String getStory() {
        String result = replace(story);
        if (StringUtils.isBlank(result))
            result = this.file.getParentFile().getName();
        return result;
    }

}
