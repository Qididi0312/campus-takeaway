package com.zhishan.zhishantakeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺营业状态
 * 这张表永远只有一行（id = 1），用来记录“营业中 / 休息中”
 * 不设计成多行是因为整个系统只有一个店铺，多行反而需要额外条件去区分哪一行有效
 */
@Data
@TableName("shop_status")
public class ShopStatus {

    // 固定为 1，所以用 IdType.INPUT：由代码指定主键，而不是数据库自增
    @TableId(type = IdType.INPUT)
    private Integer id;

    // 1 = 营业中，0 = 休息中
    private Integer status;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
