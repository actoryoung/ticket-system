package com.ticketsystem.filter;

import com.ticketsystem.common.ResultCode;
import com.ticketsystem.exception.BusinessException;
import com.ticketsystem.util.JwtUtil;
import com.ticketsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 从请求头中获取Token
            String token = getTokenFromRequest(request);

            // 2. 验证Token并设置认证信息
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到Security上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 设置到ThreadLocal
                SecurityUtil.setUserId(userId);
                SecurityUtil.setUsername(username);
                SecurityUtil.setRole(role);

                log.debug("JWT认证成功, userId={}, username={}, role={}", userId, username, role);
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
            SecurityUtil.clear();
        }

        filterChain.doFilter(request, response);

        // 请求结束后清理ThreadLocal
        SecurityUtil.clear();
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
