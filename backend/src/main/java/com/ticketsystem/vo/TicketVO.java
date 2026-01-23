package com.ticketsystem.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单详情VO
 */
@Data
public class TicketVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
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
     * 工单分类
     */
    private String category;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理人姓名
     */
    private String handlerName;

    /**
     * 附件URL列表
     */
    private List<String> attachmentUrls;

    /**
     * 解决方案
     */
    private String resolution;

    /**
     * 解决时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedTime;

    /**
     * 关闭时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
