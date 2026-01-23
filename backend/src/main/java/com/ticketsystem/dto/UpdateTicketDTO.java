package com.ticketsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新工单DTO
 */
@Data
public class UpdateTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long id;

    /**
     * 工单标题
     */
    @NotBlank(message = "工单标题不能为空")
    private String title;

    /**
     * 工单描述
     */
    private String description;

    /**
     * 工单分类
     */
    @NotBlank(message = "工单分类不能为空")
    private String category;

    /**
     * 优先级
     */
    @NotBlank(message = "优先级不能为空")
    private String priority;
}
