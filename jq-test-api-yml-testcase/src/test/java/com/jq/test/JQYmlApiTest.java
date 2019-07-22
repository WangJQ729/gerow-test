package com.jq.test;

import com.jq.test.task.YmlTestSuite;
import com.jq.test.task.ITestClass;
import com.jq.test.task.ITestSuite;
import com.jq.test.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Factory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

public class JQYmlApiTest extends JQAbstractApiTest {

    public JQYmlApiTest(ITestClass testClass) {
        super(testClass);
    }

    public static class JQApiTestFactory {
        @Factory
        public static Object[] dataFactory() {
            ITestSuite testSuite = YmlTestSuite.getInstance(Objects.requireNonNull(JQYmlApiTest.class.getClassLoader().getResource("testCase")).getFile());
            return testSuite.getTestClass().parallelStream()
                    //根据sheetName过滤测试
                    .filter(ITestClass::enable)
                    .map(JQApiTestFactory::buildTestClass).toArray();
        }

        private static Object buildTestClass(ITestClass testClass) {
            Map<String, String> params = testClass.getParams();
            //生成测试类的名称
            String className = testClass.getName().replace(".", "");
            if (params.containsKey("classNum") && !StringUtils.equals(params.get("classNum"), "0")) {
                className = className + "_" + params.get("classNum");
            }
            ClassPool pool = ClassPool.getDefault();
            CtClass ct = pool.makeClass(className);//创建类
            try {
                //生成构造方法并实例化
                JavassistUtils.setConstructor(pool, className, ct, JQYmlApiTest.class);
                ct.writeFile("target/test-classes");
                Class<?> aClass = ct.toClass();
                Constructor<?> method = aClass.getConstructor(ITestClass.class);
                return method.newInstance(testClass);
            } catch (Exception ignored) {
            }
            return new JQYmlApiTest(testClass);
        }
    }
}
