package com.book.auth;

/**
 * 保存当前请求的登录用户，由 AuthInterceptor 写入并在请求结束后清理。
 */
public final class UserContext {

    private static final ThreadLocal<SessionUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(SessionUser user) {
        HOLDER.set(user);
    }

    public static SessionUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
