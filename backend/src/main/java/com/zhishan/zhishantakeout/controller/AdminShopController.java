package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.ShopStatus;
import com.zhishan.zhishantakeout.mapper.ShopStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理端：营业 / 休息切换
 * 路径前缀 /api/admin/shop
 * 需要 token + role=1，由 JwtInterceptor + AdminInterceptor 共同校验
 */
@RestController
@RequestMapping("/api/admin/shop")
public class AdminShopController {

    @Autowired
    private ShopStatusMapper shopStatusMapper;

    /**
     * PUT /api/admin/shop/status
     * 请求体：{ "status": 0 } 休息 / { "status": 1 } 营业
     */
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("status 必须是 0 或 1");
        }

        ShopStatus row = shopStatusMapper.selectById(1);
        if (row == null) {
            // 表里没有数据时补一条，避免第一次使用时因为缺行而失败
            row = new ShopStatus();
            row.setId(1);
            row.setStatus(status);
            row.setUpdatedAt(LocalDateTime.now());
            shopStatusMapper.insert(row);
        } else {
            row.setStatus(status);
            row.setUpdatedAt(LocalDateTime.now());
            shopStatusMapper.updateById(row);
        }

        return Result.success(status == 1 ? "已开始营业" : "已进入休息");
    }
}
