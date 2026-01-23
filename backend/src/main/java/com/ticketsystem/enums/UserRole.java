package com.ticketsystem.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {

    /**
     * 普通用户
     */
    USER("USER", "普通用户"),

    /**
     * 处理人员
     */
    HANDLER("HANDLER", "处理人员"),

    /**
     * 管理员
     */
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}
