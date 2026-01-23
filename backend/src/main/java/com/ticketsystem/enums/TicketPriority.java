package com.ticketsystem.enums;

/**
 * 工单优先级枚举
 */
public enum TicketPriority {

    /**
     * 低
     */
    LOW("LOW", "低"),

    /**
     * 中
     */
    MEDIUM("MEDIUM", "中"),

    /**
     * 高
     */
    HIGH("HIGH", "高"),

    /**
     * 紧急
     */
    URGENT("URGENT", "紧急");

    private final String code;
    private final String description;

    TicketPriority(String code, String description) {
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
    public static TicketPriority fromCode(String code) {
        for (TicketPriority priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority code: " + code);
    }
}
