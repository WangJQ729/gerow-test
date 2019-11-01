package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YmlTestClass extends AbstractTestClass {
    @Override
    public boolean enable() {
        boolean feature = TestUtils.isRun(this.getStory(), System.getProperty("test.feature"));
        boolean name = TestUtils.isRun(this.getName(), System.getProperty("test.file.name"));
        return feature && name;
    }
}