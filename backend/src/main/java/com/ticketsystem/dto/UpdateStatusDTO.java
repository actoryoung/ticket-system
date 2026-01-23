package com.ticketsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新状态DTO
 */
@Data
public class UpdateStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新状态
     */
    @NotBlank(message = "状态不能为空")
    private String status;

    /**
     * 备注说明
     */
    private String remark;
}
