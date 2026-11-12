package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.Orders;
import com.zhishan.zhishantakeout.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单管理接口（管理员端）
 * 路径前缀 /api/admin/orders
 */
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrdersMapper ordersMapper;

    /** GET /api/admin/orders — 所有订单列表 */
    @GetMapping
    public Result<List<Orders>> list() {
        List<Orders> list = ordersMapper.selectList(null);
        return Result.success(list);
    }

    /** GET /api/admin/orders/{id} — 订单详情 */
    @GetMapping("/{id}")
    public Result<Orders> detail(@PathVariable Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 更新订单状态（接单、完成等）
     * PUT /api/admin/orders/{id}/status
     * 请求体：{ "status": 2 }
     * 约定 status：0=待处理 1=已接单 2=配送中 3=已完成 4=已取消
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("缺少 status");
        }
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        order.setStatus(status);
        ordersMapper.updateById(order);
        return Result.success("状态更新成功");
    }
}