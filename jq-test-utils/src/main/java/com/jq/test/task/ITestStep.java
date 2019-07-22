package com.jq.test.task;

import org.springframework.http.ResponseEntity;

public interface ITestStep {

    /**
     * 执行测试
     */
    void doing();

    /**
     * 执行数据校验
     *
     * @param entity 响应内容
     * @param <T>    响应类型
     */
    <T> void check(ResponseEntity<T> entity);

    /**
     * 执行参数提取
     *
     * @param entity 响应内容
     * @param <T>    响应体类型
     */
    <T> void saveParam(ResponseEntity<T> entity);

    /**
     * 保存参数
     */
    void saveParam();

    /**
     * 获取步骤名称
     *
     * @return 步骤名称
     */
    String getName();

    /**
     * 获取byName
     *
     * @return 所需复制的测试名称
     */
    String getByName();

    String replace(String content);
}
