package com.jq.test.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @GetMapping(value = "/login")
    public Map login(@RequestParam("username") String username,
                     @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(username) && "123456".equals(password)) {
            map.put("msg", "登录成功！");
            map.put("token", "654321");
            return map;
        } else {
            map.put("msg", "用户名密码错误");
            return map;
        }
    }
}
