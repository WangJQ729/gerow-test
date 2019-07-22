package com.jq.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 拦截器，登录检查
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    // 目标方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        // 如果获取的request的session中的loginUser参数为空（未登录），就返回登录页，否则放行访问
        if (StringUtils.equals(token, "654321")) {
            // 已登录，放行
            return true;
        } else {
            // 未登录，给出错误信息，
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out;
            try {
                JSONObject res = new JSONObject();
                res.put("success", false);
                res.put("msg", "用户未登录！");
                out = response.getWriter();
                out.append(res.toString());
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
            }
            // 获取request返回页面到登录页
            return false;
        }
    }
}
