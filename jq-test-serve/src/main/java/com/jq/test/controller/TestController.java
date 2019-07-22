package com.jq.test.controller;

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
    @ResponseBody
    public Map get() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "jqTest");
        return map;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Map post(@RequestBody @Valid PostBody body, BindingResult bindingResult) {
        Map<String, Object> map = new HashMap<>();
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
            map.put("msg", msg);
            return map;
        }
        double result = body.getNum1() + body.getNum2();
        map.put("result", result);
        return map;
    }
}
