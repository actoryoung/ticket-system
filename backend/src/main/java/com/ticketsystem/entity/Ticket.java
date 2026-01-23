package com.ticketsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单实体类
 */
@Data
@TableName("tickets")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号
     */
    private String ticketNo;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 工单描述
     */
    private String description;

    /**
     * 工单分类: BUG-缺陷, FEATURE-功能需求, SUPPORT-技术支持, OTHER-其他
     */
    private String category;

    /**
     * 优先级: LOW-低, MEDIUM-中, HIGH-高, URGENT-紧急
     */
    private String priority;

    /**
     * 状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已完成, CLOSED-已关闭
     */
    private String status;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 附件URL列表（JSON数组）
     */
    private String attachmentUrls;

    /**
     * 解决方案
     */
    private String resolution;

    /**
     * 解决时间
     */
    private LocalDateTime resolvedTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closedTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人信息（非数据库字段）
     */
    @TableField(exist = false)
    private User creator;

    /**
     * 处理人信息（非数据库字段）
     */
    @TableField(exist = false)
    private User handler;
}
