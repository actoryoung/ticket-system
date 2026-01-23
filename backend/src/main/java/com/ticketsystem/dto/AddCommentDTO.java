package com.ticketsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 添加评论DTO
 */
@Data
public class AddCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 附件URL列表
     */
    private List<String> attachmentUrls;

    /**
     * 是否内部评论
     */
    private Integer isInternal;
}
