package com.ticketsystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建工单DTO
 */
@Data
public class CreateTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单标题
     */
    @NotBlank(message = "工单标题不能为空")
    private String title;

    /**
     * 工单描述
     */
    @NotBlank(message = "工单描述不能为空")
    private String description;

    /**
     * 工单分类
     */
    @NotBlank(message = "工单分类不能为空")
    private String category;

    /**
     * 优先级
     */
    @NotNull(message = "优先级不能为空")
    private String priority;

    /**
     * 附件URL列表
     */
    private List<String> attachmentUrls;
}
