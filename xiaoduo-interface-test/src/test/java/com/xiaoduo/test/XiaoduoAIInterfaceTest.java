package com.xiaoduo.test;

import com.jq.test.JQAbstractApiTest;
import com.jq.test.task.ITestClass;
import com.jq.test.task.ITestSuite;
import com.jq.test.task.YmlTestSuite;
import com.jq.test.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Factory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;

public class XiaoduoAIInterfaceTest extends JQAbstractApiTest {

    public XiaoduoAIInterfaceTest(ITestClass testClass) {
        super(testClass);
    }

    public static class JQApiTestFactory {
        @Factory
        public static Object[] dataFactory() throws UnsupportedEncodingException {
            String platform = System.getProperty("platform");
            String features = System.getProperty("features");
            String testDir = "testCase";
            if (StringUtils.isNotBlank(platform)) {
                testDir = testDir + "/" + platform;
                if (StringUtils.isNotBlank(features) && !StringUtils.equals(features, "ALL")) {
                    testDir = testDir + "/" + features;
                }
            }
            String testDirPath = Objects.requireNonNull(XiaoduoAIInterfaceTest.class.getClassLoader().getResource(testDir)).getFile();
            ITestSuite testSuite = YmlTestSuite.getInstance(URLDecoder.decode(testDirPath, "UTF-8"));
            return testSuite.getTestClass().parallelStream()
                    //根据sheetName过滤测试
                    .filter(ITestClass::enable)
                    .map(JQApiTestFactory::buildTestClass).toArray();
        }

        private static Object buildTestClass(ITestClass testClass) {
            Map<String, String> params = testClass.getParams();
            //生成测试类的名称
            String feature = StringUtils.isNotBlank(testClass.getFeature()) ? testClass.getFeature() : testClass.getFile().getParentFile().getName();
            String className = feature + "_" + testClass.getName().replace(".", "");
            if (params.containsKey("classNum") && !StringUtils.equals(params.get("classNum"), "00")) {
                className = className + "_" + params.get("classNum");
            }
            ClassPool pool = ClassPool.getDefault();
            CtClass ct = pool.makeClass(className);//创建类
            try {
                //生成构造方法并实例化
                JavassistUtils.setConstructor(pool, className, ct, XiaoduoAIInterfaceTest.class);
                ct.writeFile("target/test-classes");
                Class<?> aClass = ct.toClass();
                Constructor<?> method = aClass.getConstructor(ITestClass.class);
                return method.newInstance(testClass);
            } catch (Exception ignored) {
            }
            return new XiaoduoAIInterfaceTest(testClass);
        }
    }
}
