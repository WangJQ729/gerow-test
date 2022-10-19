package com.gerow.test.utils.json;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.gerow.test.task.ITest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ITestNameSerializer implements ObjectSerializer {
    public static final ITestNameSerializer instance = new ITestNameSerializer();

    public ITestNameSerializer() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            if (object instanceof ArrayList) {
                List<String> collect = ((ArrayList<ITest>) object).stream().map(o -> o.getName()).collect(Collectors.toList());
                out.write(collect);
            }
        }
    }
}