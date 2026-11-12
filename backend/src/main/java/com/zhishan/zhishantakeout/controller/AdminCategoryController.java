package com.zhishan.zhishantakeout.controller;

import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.Category;
import com.zhishan.zhishantakeout.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理接口（管理员端）
 * 路径前缀 /api/admin/categories
 */
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    /** GET /api/admin/categories — 所有分类 */
    @GetMapping
    public Result<List<Category>> list() {
        List<Category> list = categoryMapper.selectList(null);
        return Result.success(list);
    }

    /** POST /api/admin/categories — 新增分类，请求体：{ "name": "...", "sort": 1 } */
    @PostMapping
    public Result<String> add(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        categoryMapper.insert(category);
        return Result.success("新增成功");
    }

    /** PUT /api/admin/categories — 修改分类，请求体必须带 id */
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        if (category.getId() == null) {
            return Result.error("缺少分类ID");
        }
        Category exist = categoryMapper.selectById(category.getId());
        if (exist == null) {
            return Result.error("分类不存在");
        }
        categoryMapper.updateById(category);
        return Result.success("修改成功");
    }

    /** DELETE /api/admin/categories/{id} — 删除分类 */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Category exist = categoryMapper.selectById(id);
        if (exist == null) {
            return Result.error("分类不存在");
        }
        categoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}