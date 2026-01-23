package com.ticketsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticketsystem.dto.LoginDTO;
import com.ticketsystem.entity.User;
import com.ticketsystem.vo.LoginVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录响应（包含Token和用户信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 根据ID查询用户信息（不包含密码）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
}
