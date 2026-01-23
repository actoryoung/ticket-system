package com.ticketsystem.controller;

import com.ticketsystem.common.Result;
import com.ticketsystem.dto.LoginDTO;
import com.ticketsystem.service.UserService;
import com.ticketsystem.util.SecurityUtil;
import com.ticketsystem.vo.LoginVO;
import com.ticketsystem.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求, username={}", loginDTO.getUsername());
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user-info")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtil.getUserId();
        log.info("获取当前用户信息, userId={}", userId);
        UserVO userVO = new UserVO();
        if (userId != null) {
            userVO = new UserVO();
            var user = userService.getUserById(userId);
            if (user != null) {
                userVO.setId(user.getId());
                userVO.setUsername(user.getUsername());
                userVO.setRealName(user.getRealName());
                userVO.setEmail(user.getEmail());
                userVO.setPhone(user.getPhone());
                userVO.setRole(user.getRole());
                userVO.setDepartment(user.getDepartment());
                userVO.setCreateTime(user.getCreateTime());
            }
        }
        return Result.success(userVO);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户退出登录, userId={}", SecurityUtil.getUserId());
        // JWT是无状态的，客户端删除Token即可
        return Result.success();
    }
}
