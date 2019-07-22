package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YmlTestClass extends AbstractTestClass {
    @Override
    public boolean enable() {
        return TestUtils.isRun(this.getName(), System.getProperty("test.file.name"));
    }
}