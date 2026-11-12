package com.zhishan.zhishantakeout.common;

/**
 * 当前登录用户上下文
 * 用 ThreadLocal 存，线程内的 Controller/Service 都能直接取到当前用户
 * 请求结束后由拦截器清理，防止内存泄漏
 */
public class UserContext {

    // ThreadLocal：每个线程有自己独立的副本，互不干扰
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    /** 拦截器解析 token 后调用，把用户信息塞进当前线程 */
    public static void set(Long userId, String username, Integer role) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static Integer getRole() {
        return ROLE.get();
    }

    /** 判断当前用户是不是管理员（role=1） */
    public static boolean isAdmin() {
        Integer role = ROLE.get();
        return role != null && role == 1;
    }

    /** 请求结束时清理，ThreadLocal 不会自动释放，必须手动清 */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}