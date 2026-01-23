package com.ticketsystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 工单查询DTO
 */
@Data
public class TicketQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer current = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 状态
     */
    private String status;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 分类
     */
    private String category;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 处理人ID
     */
    private Long handlerId;
}
