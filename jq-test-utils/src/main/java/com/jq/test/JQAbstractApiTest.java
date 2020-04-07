package com.jq.test;

import com.jq.test.listener.AllureListener;
import com.jq.test.listener.CaseListListener;
import com.jq.test.listener.FeishuListener;
import com.jq.test.task.ITest;
import com.jq.test.task.ITestClass;
import com.jq.test.task.ITestMethod;
import com.jq.test.task.JQTest;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Listeners({AllureListener.class,
        FeishuListener.class,
        CaseListListener.class
})
public abstract class JQAbstractApiTest extends AbstractTestBase implements JQTest, org.testng.ITest {

    @Getter
    private ITestClass testClass;

    private String testName;

    @BeforeSuite
    public void setUpBeforeSuite() {
        testClass.getTestSuite().setUp();
    }

    @BeforeClass
    public void setUpBeforeClass() {
        testClass.setUpBeforeClass();
    }

    @BeforeMethod
    public void setUpBeforeMethod(Object[] param, Method method) {
        this.testName = method.getName();
        for (Object o : param) {
            if (o instanceof ITestMethod) {
                ITestMethod testMethod = (ITestMethod) o;
                this.testName = testMethod.getName();
            }
        }
        logger.info("-----------------------------------------------------------");
        logger.info("开始执行：" + testName);
        testClass.setUp();
    }


    public JQAbstractApiTest(ITestClass testClass) {
        this.testClass = testClass;
    }

    @DataProvider
    public Iterator<Object[]> data() {
        List<ITestMethod> testMethods = testClass.getTestMethods().parallelStream()
                //根据测试名称过来测试
                .filter(ITest::enable)
                .flatMap(this::buildTestMethods)
                .collect(Collectors.toList());
        return testMethods.stream().map(o -> new Object[]{o}).iterator();
    }

    private Stream<? extends ITestMethod> buildTestMethods(ITestMethod testMethod) {
        List<ITestMethod> methods = new ArrayList<>();
        String executionCount = testMethod.replace(testMethod.getTestClass().getParams().get("executionCount"));
        if (StringUtils.isNoneBlank(executionCount)) {
            int count = Integer.parseInt(executionCount);
            for (int i = 0; i < count; i++) {
                methods.add(testMethod);
            }
        } else {
            methods.add(testMethod);
        }
        return methods.stream();
    }

    @Test(dataProvider = "data")
    public void doing(ITestMethod test) {
        test.doing();
    }

    @AfterSuite
    public void tearDownAfterSuite() {
        testClass.getTestSuite().tearDown();
    }

    @AfterClass
    public void tearDownAfterClass() {
        testClass.tearDownAfterClass();
    }

    @AfterMethod
    public void tearDownAfterMethod(ITestResult result) {
        try {
            testClass.tearDown();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        logger.info("测试结束：" + testName);
        logger.info("测试结果：" + (result.getStatus() == 1 ? "Pass" : "Fail"));
        logger.info("-----------------------------------------------------------");
    }

    @Override
    public String getTestName() {
        return testName;
    }
}
