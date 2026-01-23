package com.ticketsystem.util;

/**
 * 安全工具类 - 存储当前登录用户信息
 */
public class SecurityUtil {

    /**
     * ThreadLocal 存储用户ID
     */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * ThreadLocal 存储用户名
     */
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /**
     * ThreadLocal 存储角色
     */
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置当前角色
     */
    public static void setRole(String role) {
        ROLE.set(role);
    }

    /**
     * 获取当前角色
     */
    public static String getRole() {
        return ROLE.get();
    }

    /**
     * 清理ThreadLocal
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
