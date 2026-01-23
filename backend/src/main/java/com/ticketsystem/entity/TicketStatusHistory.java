package com.ticketsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单状态历史实体类
 */
@Data
@TableName("ticket_status_history")
public class TicketStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单ID
     */
    private Long ticketId;

    /**
     * 原状态
     */
    private String oldStatus;

    /**
     * 新状态
     */
    private String newStatus;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
