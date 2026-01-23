package com.ticketsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单评论实体类
 */
@Data
@TableName("ticket_comments")
public class TicketComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单ID
     */
    private Long ticketId;

    /**
     * 评论人ID
     */
    private Long userId;

    /**
     * 评论人姓名
     */
    private String userName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 附件URL列表（JSON数组）
     */
    private String attachmentUrls;

    /**
     * 是否内部评论: 0-否, 1-是
     */
    private Integer isInternal;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 评论人信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
}
