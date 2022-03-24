package com.gerow.test.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;

public class YmlFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            //只要yml文件
            return StringUtils.endsWith(file.getName(), ".yml");
        }
    }
}
