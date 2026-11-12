package com.zhishan.zhishantakeout.config;

import com.zhishan.zhishantakeout.interceptor.AdminInterceptor;
import com.zhishan.zhishantakeout.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 层配置：注册拦截器
 * 拦截器执行顺序：按 addInterceptor 的先后顺序
 *   1. JwtInterceptor 先跑（校验登录）
 *   2. AdminInterceptor 后跑（校验管理员，依赖 JwtInterceptor 填的 UserContext）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 第一个：登录拦截器
        // 拦所有 /api/**，但放行登录注册和公开查询接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/login",           // 登录
                        "/api/register",        // 注册
                        "/api/categories",      // 公开查分类
                        "/api/categories/**",
                        "/api/dishes",          // 公开查菜品
                        "/api/dishes/**",
                        "/api/shop/status"      // 公开查营业状态（未登录也要能看到“休息中”）
                );

        // 第二个：管理员拦截器，只拦 /api/admin/**
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}