package com.jq.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author WangJQ
 * @Description
 * @Date created in 17:02 2018/8/1
 */
@SpringBootApplication
public class SpringbootApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration ir = registry.addInterceptor(new LoginHandlerInterceptor());
        ir.addPathPatterns("/**");
        ir.excludePathPatterns("/login");
    }
}
