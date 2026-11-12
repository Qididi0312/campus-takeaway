package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试用接口，用来验证后端是否正常运行
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * 健康检查接口
     * GET /api/ping
     */
    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new HashMap<>();
        data.put("msg", "pong");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }
}