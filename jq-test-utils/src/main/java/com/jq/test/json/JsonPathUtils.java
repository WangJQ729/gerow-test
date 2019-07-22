package com.jq.test.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.commons.lang.StringUtils;

public class JsonPathUtils {

    /**
     * @param json     json对象
     * @param jsonPath jsonPath
     * @param options  参数
     * @param <T>      提取参数的类型
     * @return 提取到的参数
     */
    public static <T> T read(Object json, String jsonPath, Option... options) {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(options);
        return JsonPath.using(configuration).parse(json).read(jsonPath);
    }

    /**
     * @param json     json对象
     * @param jsonPath jsonPath
     * @param options  参数
     * @param <T>      提取参数的类型
     * @return 提取到的参数
     */
    public static <T> T read(String json, String jsonPath, Option... options) {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(options);
        return JsonPath.using(configuration).parse(json).read(jsonPath);
    }

    /**
     * @param json     json对象
     * @param jsonPath jsonPath
     * @param value    所要赋的值
     * @param options  参数
     * @return 赋值后的json对象
     */
    public static String put(String json, String jsonPath, Object value, Option... options) {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(options);
        String path = StringUtils.substringBeforeLast(jsonPath, ".");
        String key = StringUtils.substringAfterLast(jsonPath, ".");
        return JsonPath.using(configuration).parse(json).put(path, key, value).jsonString();
    }

    /**
     * @param json     json对象
     * @param jsonPath 所要删除的jsonPath
     * @param options  参数
     * @return 删除参数后的对象
     */
    public static String delete(String json, String jsonPath, Option... options) {
        Configuration configuration = Configuration.defaultConfiguration().addOptions(options);
        return JsonPath.using(configuration).parse(json).delete(jsonPath).jsonString();
    }
}
