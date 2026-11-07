package com.book.auth;

/**
 * 登录会话中保存的最小用户信息。
 */
public record SessionUser(Integer id, String username, String name, String role) {
}
