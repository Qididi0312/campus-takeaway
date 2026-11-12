package com.zhishan.zhishantakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishan.zhishantakeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}