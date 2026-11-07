package com.book.auth;

import com.book.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简化版内存令牌存储，适合单人课程项目。
 * 服务重启后令牌失效，前端重新登录即可。
 */
@Component
public class TokenStore {

    private static final long EXPIRE_MILLIS = 12 * 60 * 60 * 1000L;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String create(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String role = "admin".equals(user.getUsername()) ? "admin" : "user";
        sessions.put(token, new Session(
                new SessionUser(user.getId(), user.getUsername(), user.getName(), role),
                System.currentTimeMillis() + EXPIRE_MILLIS));
        return token;
    }

    public SessionUser resolve(String authorization) {
        String token = extractToken(authorization);
        if (token == null) {
            return null;
        }
        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }
        if (session.expireAt < System.currentTimeMillis()) {
            sessions.remove(token);
            return null;
        }
        return session.user;
    }

    public void remove(String authorization) {
        String token = extractToken(authorization);
        if (token != null) {
            sessions.remove(token);
        }
    }

    public static String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String value = authorization.trim();
        if (value.startsWith("Bearer ")) {
            value = value.substring(7).trim();
        }
        return value.isEmpty() ? null : value;
    }

    private static class Session {
        private final SessionUser user;
        private final long expireAt;

        private Session(SessionUser user, long expireAt) {
            this.user = user;
            this.expireAt = expireAt;
        }
    }
}
