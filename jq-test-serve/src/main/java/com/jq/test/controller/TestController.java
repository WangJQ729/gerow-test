package com.jq.test.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TestController {
    @RequestMapping("/get")
    public Map get() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "jqTest");
        return map;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Result post(@RequestBody @Valid PostBody body, BindingResult bindingResult) {
        Result result = new Result();
        Map<String, Object> map = new HashMap<>();
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
            result.setCode(20000);
            map.put("msg", msg);
        } else {
            result.setCode(10000);
            map.put("result", body.getNum1() + body.getNum2());
        }
        result.setData(map);
        return result;
    }

    @Data
    public class Result {
        private int code;
        private Object data;

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }
    }
}
