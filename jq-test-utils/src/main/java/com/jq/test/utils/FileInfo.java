package com.jq.test.utils;

import lombok.Data;

/**
 * 上传文件的信息
 */
@Data
public class FileInfo {
    /**
     * 上传文件form表达的key
     */
    private String key;
    /**
     * 文件地址
     */
    private String path;
}
