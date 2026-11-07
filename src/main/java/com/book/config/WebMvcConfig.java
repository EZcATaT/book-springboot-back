package com.book.config;

import com.book.auth.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册登录拦截器。
 * /api/** 为 RESTful 接口，/user/**、/book/**、/borrow/** 为兼容旧路径。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/user/**", "/book/**", "/borrow/**", "/api/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/logout",
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/logout",
                        "/api/health",
                        "/health"
                );
    }
}
