package com.gerow.test;

import com.gerow.test.utils.TestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

@SpringBootTest(classes = SpringbootApplication.class)
public class AbstractTestBase extends AbstractTestNGSpringContextTests {
    static {
        TestUtils.initJMeterFunctions();
    }

    @BeforeSuite
    protected void springTestContextBeforeTestClass() throws Exception {
        super.springTestContextBeforeTestClass();
    }

    @BeforeSuite(dependsOnMethods = {"springTestContextBeforeTestClass"})
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
    }
}
