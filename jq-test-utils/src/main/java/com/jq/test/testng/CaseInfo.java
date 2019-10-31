package com.jq.test.testng;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class CaseInfo {
    private String case_name;
    private String description;
    private String story;
    private String feature;
    private String severity;
    private String creator;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
