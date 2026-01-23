package com.ticketsystem.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 角色
     */
    private String role;

    /**
     * 部门
     */
    private String department;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
