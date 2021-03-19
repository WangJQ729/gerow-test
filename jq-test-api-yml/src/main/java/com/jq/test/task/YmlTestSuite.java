package com.jq.test.task;

import com.jq.test.entity.YamlFc;
import com.jq.test.entity.YmlFileFilter;
import com.jq.test.entity.YmlTestClassEntity;
import com.jq.test.utils.TestUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YmlTestSuite extends AbstractTestSuite {

    private static YmlTestSuite testSuite;

    public static YmlTestSuite getInstance(String dirPath) {
        if (testSuite == null) {
            testSuite = new YmlTestSuite(dirPath);
        }
        return testSuite;

    }

    private YmlTestSuite(String pathDir) {
        setFile(new File(pathDir));
        List<File> files = TestUtils.getAllFile(getFile(), new YmlFileFilter());
        setTestClass(files.stream().flatMap(file
                -> new YamlFc<>(YmlTestClassEntity.class).read(file).build(this, file).stream()
        ).collect(Collectors.toList()));
    }


    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public void addHeartbeat(ITestMethod testMethod) {
        this.setHeartbeat(Collections.singletonList(testMethod));
    }
}
