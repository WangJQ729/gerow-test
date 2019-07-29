package com.jq.test.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

public enum DataType {
    /**
     * 响应体JSON类型
     */
    JSON(JSONObject.class),
    /**
     * 响应体XML类型
     */
    XML(String.class),
    EXCEL(byte[].class),
    /**
     * 默认JSON
     */
    DEFAULT(String.class);

    @Getter
    private Class<?> type;

    DataType(Class type) {
        this.type = type;
    }
}
