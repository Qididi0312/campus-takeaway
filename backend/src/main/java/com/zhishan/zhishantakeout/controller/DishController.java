package com.zhishan.zhishantakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zhishan.zhishantakeout.entity.Dish;

import java.util.List;

/**
 * 菜品相关接口（用户端）
 * 只返回已上架的菜品（status = 1）
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishMapper dishMapper;

    /**
     * 菜品列表（用户端）
     * GET /api/dishes
     * 只查 status = 1 的，下架菜品用户不应该看到
     */
    @GetMapping
    public Result<List<Dish>> list() {
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);            // WHERE status = 1（上架）
        List<Dish> list = dishMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 菜品详情
     * GET /api/dishes/{id}
     */
    @GetMapping("/{id}")
    public Result<Dish> detail(@PathVariable Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null || dish.getStatus() != 1) {
            return Result.error("菜品不存在或已下架");
        }
        return Result.success(dish);
    }
}