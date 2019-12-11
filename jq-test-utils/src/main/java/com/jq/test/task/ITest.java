package com.jq.test.task;

import java.util.Map;

public interface ITest {
    /**
     * 获取名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 保存参数
     *
     * @param key   参数名称
     * @param value 参数值
     */
    void save(String key, String value);

    /**
     * 替换参数
     *
     * @param content 所要替换的内容
     * @return 替换后的内容
     */
    String replace(String content);

    /**
     * 测试是否执行
     *
     * @return 是否执行
     */
    boolean enable();

    Map<String, String> getParams();
}
