package com.zhishan.zhishantakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishan.zhishantakeout.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
