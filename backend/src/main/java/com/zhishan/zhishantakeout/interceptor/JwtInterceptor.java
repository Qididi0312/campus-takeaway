package com.zhishan.zhishantakeout.interceptor;

import com.zhishan.zhishantakeout.common.UserContext;
import com.zhishan.zhishantakeout.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 所有请求先过这里，从请求头 Authorization 取 token 并解析
 * 解析成功 → 把用户信息放进 UserContext，放行
 * 解析失败 → 返回 401 JSON
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 在 Controller 执行之前跑
     * 返回 true = 放行；返回 false = 中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前端约定把 token 放在 Authorization 请求头里
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\",\"data\":null}");
            return false;
        }

        try {
            // 验签 + 解析 payload
            Claims claims = jwtUtil.parse(token);
            Long userId = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);
            Integer role = claims.get("role", Integer.class);
            // 存进 ThreadLocal，后面 Controller 随便用
            UserContext.set(userId, username, role);
            return true;
        } catch (Exception e) {
            // 签名不匹配、过期、格式不对都走这里
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期\",\"data\":null}");
            return false;
        }
    }

    /** 请求完全结束后（包括视图渲染），清理 ThreadLocal，防止线程池复用时脏数据 */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}