package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.Dish;
import com.zhishan.zhishantakeout.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜品管理接口（管理员端）
 * 路径前缀 /api/admin/dishes
 * 需要 token + role=1，由 AdminInterceptor 校验
 * 和用户端 DishController 的区别：这里能看到下架菜品、能改状态
 */
@RestController
@RequestMapping("/api/admin/dishes")
public class AdminDishController {

    @Autowired
    private DishMapper dishMapper;

    /**
     * 菜品列表（管理员看全部，包括下架的）
     * GET /api/admin/dishes
     */
    @GetMapping
    public Result<List<Dish>> list() {
        List<Dish> list = dishMapper.selectList(null);  // null = 不加条件，查全部
        return Result.success(list);
    }

    /**
     * 新增菜品
     * POST /api/admin/dishes
     * 请求体：Dish 的字段（id 不用传，status 默认 1 上架）
     */
    @PostMapping
    public Result<String> add(@RequestBody Dish dish) {
        dish.setStatus(1);   // 新增默认上架
        dishMapper.insert(dish);
        return Result.success("新增成功");
    }

    /**
     * 修改菜品
     * PUT /api/admin/dishes
     * 请求体里必须带 id
     */
    @PutMapping
    public Result<String> update(@RequestBody Dish dish) {
        if (dish.getId() == null) {
            return Result.error("缺少菜品ID");
        }
        Dish exist = dishMapper.selectById(dish.getId());
        if (exist == null) {
            return Result.error("菜品不存在");
        }
        dishMapper.updateById(dish);
        return Result.success("修改成功");
    }

    /**
     * 上架 / 下架
     * PUT /api/admin/dishes/{id}/status
     * 请求体：{ "status": 0 } 下架 / { "status": 1 } 上架
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("status 必须是 0 或 1");
        }
        Dish exist = dishMapper.selectById(id);
        if (exist == null) {
            return Result.error("菜品不存在");
        }
        exist.setStatus(status);
        dishMapper.updateById(exist);
        return Result.success(status == 1 ? "已上架" : "已下架");
    }

    /**
     * 删除菜品
     * DELETE /api/admin/dishes/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Dish exist = dishMapper.selectById(id);
        if (exist == null) {
            return Result.error("菜品不存在");
        }
        dishMapper.deleteById(id);
        return Result.success("删除成功");
    }
}