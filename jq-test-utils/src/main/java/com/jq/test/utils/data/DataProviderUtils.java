package com.jq.test.utils.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class DataProviderUtils {
    /**
     * 构造数据
     * 如果原始数据中有 dataProvider，将dataProvider中的Map加入到原始数据中
     *
     * @param data 测试数据
     * @return build后的数据
     */
    private static List<LinkedHashMap<String, Object>> buildDataProvider(LinkedHashMap<String, Object> data) {
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        if (data.containsKey("dataProvider")) {
            List<LinkedHashMap<String, Object>> dataProviders = (List<LinkedHashMap<String, Object>>) data.get("dataProvider");
            for (LinkedHashMap<String, Object> provider : dataProviders) {
                if (provider.containsKey("dataProvider")) {
                    for (LinkedHashMap<String, Object> map : buildDataProvider(provider)) {
                        LinkedHashMap<String, Object> newData = new LinkedHashMap<>(data);
                        newData.putAll(map);
                        newData.remove("dataProvider");
                        result.add(newData);
                    }
                } else {
                    LinkedHashMap<String, Object> newData = new LinkedHashMap<>(data);
                    newData.putAll(provider);
                    newData.remove("dataProvider");
                    result.add(newData);
                }
            }
        } else {
            LinkedHashMap<String, Object> newData = new LinkedHashMap<>(data);
            result.add(newData);
        }
        return result;
    }

    public static List<LinkedHashMap<String, String>> dataProvider(LinkedHashMap<String, Object> data) {
        List<LinkedHashMap<String, String>> result = new ArrayList<>();
        for (LinkedHashMap<String, Object> provider : buildDataProvider(data)) {
            boolean needAddFlag = true;
            LinkedHashMap<String, String> newData = new LinkedHashMap<>();
            for (String k : provider.keySet()) {
                Object v = provider.get(k);
                if (v instanceof Collection) {
                    ArrayList list = new ArrayList<>((Collection) v);
                    for (Object s : list) {
                        LinkedHashMap<String, Object> newProvider = new LinkedHashMap<>(provider);
                        newProvider.putAll(newData);
                        newProvider.put(k, s.toString());
                        result.addAll(dataProvider(newProvider));
                    }
                    //提前break,参数为替换完,不返回
                    needAddFlag = false;
                    break;
                } else {
                    newData.put(k, v.toString());
                }
            }
            if (needAddFlag) {
                result.add(newData);
            }
        }
        return result;
    }
}
