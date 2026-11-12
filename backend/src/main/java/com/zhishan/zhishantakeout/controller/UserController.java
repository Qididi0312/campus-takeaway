package com.zhishan.zhishantakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhishan.zhishantakeout.common.Result;
import com.zhishan.zhishantakeout.entity.User;
import com.zhishan.zhishantakeout.mapper.UserMapper;
import com.zhishan.zhishantakeout.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户账号相关接口：注册、登录
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // BCryptPasswordEncoder：Spring Security 提供的密码哈希工具
    // 注册时用它加密密码，登录时用它校验密码
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     * POST /api/register
     * 请求体 JSON：{ "username": "...", "password": "...", "phone": "..." }
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String phone = body.get("phone");

        // ===== 参数校验 =====
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            return Result.error("用户名长度 3-20 位");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (password.length() < 6) {
            return Result.error("密码至少 6 位");
        }

        // ===== 唯一性校验 =====
        // QueryWrapper.eq 就是 SQL 的 WHERE 列 = 值
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        if (userMapper.selectCount(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 手机号如果填了也检查重复
        if (phone != null && !phone.trim().isEmpty()) {
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", phone);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                return Result.error("手机号已被注册");
            }
        }

        // ===== 写入数据库 =====
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password)); // BCrypt 加密存密文，再也看不到明文了
        user.setPhone(phone);
        user.setRole(0);        // 0 = 普通用户，管理员由数据库手动设 role=1
        userMapper.insert(user);

        return Result.success("注册成功");
    }

    /**
     * 用户登录
     * POST /api/login
     * 请求体 JSON：{ "username": "...", "password": "..." }
     * 成功后返回 JWT token 和用户基本信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 按用户名查用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }
        // BCrypt 校验：matches(明文, 密文) → boolean
        if (!encoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        // 登录成功 → 生成 JWT
        String token = jwtUtil.generate(user.getId(), user.getUsername(), user.getRole());

        // 把 token 和用户基本信息返回给前端
        // 前端把 token 存 localStorage，之后每次请求放到 Authorization 头
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        return Result.success(data);
    }
}