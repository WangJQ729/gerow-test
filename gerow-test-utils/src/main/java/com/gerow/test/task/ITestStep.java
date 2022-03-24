package com.gerow.test.task;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ITestStep extends ITest {

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
     * 获取byName
     *
     * @return 所需复制的测试名称
     */
    String getByName();


    Map<String, Integer> getAssertionLength();

    void addAssertionLength(String key, int value);

}
