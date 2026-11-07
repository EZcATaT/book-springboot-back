package com.book.dto;

import lombok.Data;

/**
 * 登录请求参数，避免直接暴露实体中的密码字段。
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
