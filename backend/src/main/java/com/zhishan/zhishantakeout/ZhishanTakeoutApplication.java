package com.zhishan.zhishantakeout;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication：Spring Boot 启动类的核心注解，自动配置、组件扫描等
// @MapperScan：告诉 MyBatis-Plus 去哪个包下找 Mapper 接口
@MapperScan("com.zhishan.zhishantakeout.mapper")
@SpringBootApplication
public class ZhishanTakeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhishanTakeoutApplication.class, args);
    }

}