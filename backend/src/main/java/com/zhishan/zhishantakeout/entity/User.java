package com.zhishan.zhishantakeout.entity;

// ===== MyBatis-Plus 的注解包，用来告诉框架类和数据库表的对应关系 =====
import com.baomidou.mybatisplus.annotation.IdType;     // 主键生成策略（自增/雪花/手动等）
import com.baomidou.mybatisplus.annotation.TableField;  // 当 Java 字段名和数据库列名不一样时，指定映射
import com.baomidou.mybatisplus.annotation.TableId;       // 标记这个字段是主键
import com.baomidou.mybatisplus.annotation.TableName;     // 指定这个类对应数据库里的哪张表

import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private Integer role;

    @TableField("created_at")
    private LocalDateTime createdAt;
}