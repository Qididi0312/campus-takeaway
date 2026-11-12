package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.ShopStatus;
import com.zhishan.zhishantakeout.mapper.ShopStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端：查询店铺是否营业
 * 这个接口是公开的（在 WebMvcConfig 里放行），因为未登录用户也要能看到“休息中”
 */
@RestController
@RequestMapping("/api")
public class ShopController {

    @Autowired
    private ShopStatusMapper shopStatusMapper;

    /**
     * GET /api/shop/status
     * 返回：{ "status": 1, "open": true, "text": "营业中" }
     */
    @GetMapping("/shop/status")
    public Result<Map<String, Object>> status() {
        // 只查 id = 1 那一行；理论上一定存在，查不到时按“休息中”处理更安全
        ShopStatus row = shopStatusMapper.selectById(1);
        int status = (row == null || row.getStatus() == null) ? 0 : row.getStatus();

        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        data.put("open", status == 1);
        data.put("text", status == 1 ? "营业中" : "休息中");
        return Result.success(data);
    }
}
