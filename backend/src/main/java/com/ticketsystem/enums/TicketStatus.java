package com.ticketsystem.enums;

/**
 * 工单状态枚举
 */
public enum TicketStatus {

    /**
     * 待处理
     */
    PENDING("PENDING", "待处理"),

    /**
     * 处理中
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 已完成
     */
    RESOLVED("RESOLVED", "已完成"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String description;

    TicketStatus(String code, String description) {
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
    public static TicketStatus fromCode(String code) {
        for (TicketStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
