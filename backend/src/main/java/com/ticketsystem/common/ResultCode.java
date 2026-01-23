package com.ticketsystem.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    // 服务器错误 5xx
    ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误码 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    TOKEN_INVALID(1005, "Token无效或已过期"),
    TOKEN_EXPIRED(1006, "Token已过期"),

    // 工单相关错误码 2xxx
    TICKET_NOT_FOUND(2001, "工单不存在"),
    TICKET_STATUS_INVALID(2002, "工单状态不允许此操作"),
    TICKET_ALREADY_ASSIGNED(2003, "工单已被分配"),
    TICKET_PERMISSION_DENIED(2004, "没有权限操作此工单"),
    TICKET_COMMENT_NOT_FOUND(2005, "评论不存在"),

    // 文件上传错误码 3xxx
    FILE_UPLOAD_FAILED(3001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(3002, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(3003, "文件类型不允许");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
