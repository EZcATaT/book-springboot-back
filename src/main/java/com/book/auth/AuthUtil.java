package com.book.auth;

import com.book.common.BusinessException;

/**
 * 当前登录用户与权限判断工具。
 */
public final class AuthUtil {

    private AuthUtil() {
    }

    public static SessionUser currentUser() {
        SessionUser user = UserContext.get();
        if (user == null) {
            throw new BusinessException("请先登录");
        }
        return user;
    }

    public static boolean isAdmin() {
        return "admin".equals(currentUser().role());
    }

    public static void requireAdmin() {
        if (!isAdmin()) {
            throw new BusinessException("无权限执行该操作，仅管理员可用");
        }
    }

    public static void requireSelfOrAdmin(Integer userId) {
        SessionUser current = currentUser();
        if (!"admin".equals(current.role()) && !current.id().equals(userId)) {
            throw new BusinessException("只能查看或操作自己的数据");
        }
    }
}
