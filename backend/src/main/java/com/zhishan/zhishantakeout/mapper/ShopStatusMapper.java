package com.zhishan.zhishantakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishan.zhishantakeout.entity.ShopStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺营业状态 Mapper
 * 单表无复杂查询，直接用 MyBatis-Plus 提供的基础方法即可
 */
@Mapper
public interface ShopStatusMapper extends BaseMapper<ShopStatus> {
}
