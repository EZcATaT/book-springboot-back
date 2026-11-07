package com.book.controller;

import com.book.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口，方便启动后快速确认服务状态。
 */
@RestController
public class HealthController {

    @GetMapping({"/health", "/api/health"})
    public Result<String> health() {
        return Result.success("book-springboot-back is running");
    }
}
