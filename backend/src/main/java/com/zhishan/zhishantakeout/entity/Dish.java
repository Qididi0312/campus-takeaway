package com.zhishan.zhishantakeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class Dish {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    private BigDecimal price;

    private String description;

    private String imageUrl;

    private Integer spicy;

    private Integer stock;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}