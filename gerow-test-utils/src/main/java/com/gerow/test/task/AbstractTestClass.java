package com.gerow.test.task;

import com.alibaba.fastjson.annotation.JSONField;
import com.gerow.test.utils.TestUtils;
import com.gerow.test.utils.json.ITestNameSerializer;
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
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> testMethods = new ArrayList<>();

    public Map<String, String> params = new LinkedHashMap<>();
    @JSONField(serialize = false)
    private ITestSuite testSuite;
    @JSONField(serialize = false)
    private File file;
    private boolean enable;
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> beforeClass = new ArrayList<>();
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> afterClass = new ArrayList<>();
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> classHeartbeat = new ArrayList<>();
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> keyWord = new ArrayList<>();
    @JSONField(serializeUsing = ITestNameSerializer.class)
    private List<ITestMethod> before = new ArrayList<>();
    @JSONField(serializeUsing = ITestNameSerializer.class)
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
            try {
                testMethod.doing();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void tearDownAfterClass() {
        for (ITestMethod testMethod : afterClass) {
            try {
                testMethod.doing();
            } catch (Error ignored) {
            }
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
