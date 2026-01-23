package com.ticketsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分配工单DTO
 */
@Data
public class AssignTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 处理人ID
     */
    @NotNull(message = "处理人ID不能为空")
    private Long handlerId;
}
