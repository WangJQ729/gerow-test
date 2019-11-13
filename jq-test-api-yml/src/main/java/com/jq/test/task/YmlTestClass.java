package com.jq.test.task;

import com.jq.test.utils.TestUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YmlTestClass extends AbstractTestClass {
    @Override
    public boolean enable() {
        YmlTestClass.this.getStory();
        boolean story = TestUtils.isRun(this.getStory(), System.getProperty("story"));
        if (!story) {
            return false;
        }
        boolean storySkip = TestUtils.isSkip(this.getName(), System.getProperty("skipStory"));
        if (storySkip) {
            return false;
        }
        boolean component = TestUtils.isRun(this.getName(), System.getProperty("component"));
        if (!component) {
            return false;
        }
        return !TestUtils.isSkip(this.getName(), System.getProperty("skipComponent"));
    }
}