package com.gerow.test;

import com.gerow.test.task.YmlTestSuite;
import com.gerow.test.task.ITestClass;
import com.gerow.test.task.ITestSuite;
import com.gerow.test.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Factory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GerowAIInterfaceTest extends GerowAbstractApiTest {

    public GerowAIInterfaceTest(ITestClass testClass) {
        super(testClass);
    }

    public static class GerowApiTestFactory {
        @Factory
        public static Object[] dataFactory() throws UnsupportedEncodingException {
            String testDir = System.getProperty("testDir");
            testDir = StringUtils.isBlank(testDir) ? System.getProperty("platform") : testDir;
            testDir = StringUtils.isBlank(testDir) ? "testCase" : "testCase" + "/" + testDir;
            String testDirPath = Objects.requireNonNull(GerowAIInterfaceTest.class.getClassLoader().getResource(testDir)).getFile();
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
                    //根据sheetName过滤测试
                    .filter(ITestClass::enable)
                    .map(GerowApiTestFactory::buildTestClass).toArray();
        }

        private static Object buildTestClass(ITestClass testClass) {
            Map<String, String> params = testClass.getParams();
            //生成测试类的名称
            String feature = StringUtils.isNotBlank(testClass.getStory()) ? testClass.getStory() : testClass.getFile().getParentFile().getName();
            String className = feature + "_" + testClass.getName().replace(".", "");
            if (params.containsKey("classNum") && !StringUtils.equals(params.get("classNum"), "00")) {
                className = className + "_" + String.format("%03d", Integer.parseInt(params.get("classNum")));
            }
            ClassPool pool = ClassPool.getDefault();
            CtClass ct = pool.makeClass(className);//创建类
            try {
                //生成构造方法并实例化
                JavassistUtils.setConstructor(pool, className, ct, GerowAIInterfaceTest.class);
                ct.writeFile("target/test-classes");
                Class<?> aClass = ct.toClass();
                Constructor<?> method = aClass.getConstructor(ITestClass.class);
                return method.newInstance(testClass);
            } catch (Throwable ignored) {
            }
            return new GerowAIInterfaceTest(testClass);
        }
    }
}
