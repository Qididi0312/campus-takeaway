package com.zhishan.zhishantakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishan.zhishantakeout.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}