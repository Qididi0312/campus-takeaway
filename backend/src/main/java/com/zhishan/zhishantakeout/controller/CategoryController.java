package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.Category;
import com.zhishan.zhishantakeout.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜品分类相关接口（用户端）
 */
// @RestController：标记为 Controller，所有方法返回值自动序列化为 JSON
// @RequestMapping("/api/categories")：该 Controller 下所有接口的 URL 前缀
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    // @Autowired：Spring 自动注入 CategoryMapper 实例，不用手动 new
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类列表
     * GET /api/categories
     */
    @GetMapping
    public Result<List<Category>> list() {
        // selectList(null)：传 null 表示不加任何查询条件，等价于 SELECT * FROM category
        List<Category> list = categoryMapper.selectList(null);
        return Result.success(list);
    }
}