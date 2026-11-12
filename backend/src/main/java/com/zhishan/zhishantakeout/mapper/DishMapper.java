package com.zhishan.zhishantakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishan.zhishantakeout.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}