package com.zhishan.zhishantakeout.interceptor;

import com.zhishan.zhishantakeout.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 * 必须在 JwtInterceptor 之后执行（JwtInterceptor 先把 UserContext 填好）
 * 检查 role == 1，不是管理员直接 403
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!UserContext.isAdmin()) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无权限\",\"data\":null}");
            return false;
        }
        return true;
    }
}