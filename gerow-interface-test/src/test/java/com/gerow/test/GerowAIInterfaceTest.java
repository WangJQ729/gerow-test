package com.gerow.test;

import com.gerow.test.task.ITestClass;
import com.gerow.test.task.ITestSuite;
import com.gerow.test.task.YmlTestSuite;
import com.gerow.test.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Factory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.security.ProtectionDomain;
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
            try {
                FileUtils.deleteDirectory(new File("allure-results"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String testDir = System.getProperty("testDir");
            testDir = StringUtils.isBlank(testDir) ? System.getProperty("platform") : testDir;
            testDir = StringUtils.isBlank(testDir) ? "testCase" : "testCase" + "/" + testDir;
            String testDirPath = Objects.requireNonNull(GerowAIInterfaceTest.class.getClassLoader().getResource(testDir)).getFile();
            ITestSuite testSuite = YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
            System.out.println(testSuite.getTestClass().stream().filter(iTestClass -> StringUtils.equals(iTestClass.getFeature(), System.getProperty("features"))).map(ITestClass::getStory).distinct().collect(Collectors.joining(",")));
            System.out.println(testSuite.getTestClass().stream().filter(iTestClass -> StringUtils.equals(iTestClass.getFeature(), System.getProperty("features"))).map(ITestClass::getName).distinct().collect(Collectors.joining(",")));
            return testSuite.getTestClass().parallelStream()
                    //根据yml文件名称过滤测试
                    .filter(ITestClass::enable).map(GerowApiTestFactory::buildTestClass).toArray();
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
                // 生成构造方法并实例化
                JavassistUtils.setConstructor(pool, className, ct, GerowAIInterfaceTest.class);
                ct.writeFile("target/test-classes");
                // 将 CtClass 转换为字节码数组
                byte[] byteCode = ct.toBytecode();
                // 获取与 GerowAIInterfaceTest.class 相同包的 Lookup 实例
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(GerowAIInterfaceTest.class, MethodHandles.lookup());
                // 使用自定义类加载器实例化
                CustomClassLoader classLoader = new CustomClassLoader();
                Class<?> aClass = classLoader.defineClass(className, byteCode);
                Constructor<?> method = aClass.getConstructor(ITestClass.class);
                return method.newInstance(testClass);
            } catch (Throwable ignored) {
            }
            return new GerowAIInterfaceTest(testClass);
        }

        static class CustomClassLoader extends ClassLoader {
            public Class<?> defineClass(String name, byte[] b) {
                return super.defineClass(name, b, 0, b.length);
            }
        }
    }
}
