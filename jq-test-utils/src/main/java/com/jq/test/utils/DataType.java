package com.jq.test.utils;

public enum DataType {
    /**
     * 响应体JSON类型
     */
    JSON,
    /**
     * 响应体XML类型
     */
    XML,
    /**
     * 请求头
     */
    HEADER,

    /**
     * 常量
     */
    CONSTANT,
    /**
     * 响应状态
     */
    STATUS,
    /**
     * 手动传入参数
     */
    DATA,
    /**
     * 默认JSON
     */
    DEFAULT
}
