package com.ticketsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticketsystem.common.ResultCode;
import com.ticketsystem.dto.LoginDTO;
import com.ticketsystem.entity.User;
import com.ticketsystem.exception.BusinessException;
import com.ticketsystem.mapper.UserMapper;
import com.ticketsystem.service.UserService;
import com.ticketsystem.util.JwtUtil;
import com.ticketsystem.vo.LoginVO;
import com.ticketsystem.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 5. 构建返回结果
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);

        return new LoginVO(token, userVO);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User getUserById(Long userId) {
        User user = getById(userId);
        if (user != null) {
            // 清空密码字段
            user.setPassword(null);
        }
        return user;
    }
}
