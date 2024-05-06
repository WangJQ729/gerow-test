package com.gerow.test.task;

import com.gerow.test.entity.YamlFc;
import com.gerow.test.entity.YmlFileFilter;
import com.gerow.test.entity.YmlTestClassEntity;
import com.gerow.test.utils.TestUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YmlTestSuite extends AbstractTestSuite {

    public static YmlTestSuite getInstance(String dirPath) {
        return new YmlTestSuite(dirPath);
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
