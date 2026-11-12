package com.zhishan.zhishantakeout.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：负责 token 的生成与解析
 * 从 application.yaml 读取密钥和过期时间
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;          // 签名密钥

    @Value("${jwt.expiration}")
    private long expiration;        // 过期时间（毫秒）

    // 根据密钥生成 HMAC 签名用的 SecretKey 对象
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token
     * payload 里放 userId（subject）、username、role
     */
    public String generate(Long userId, String username, Integer role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))   // subject 存 userId
                .claim("username", username)        // 自定义 claim
                .claim("role", role)                // 角色：0=普通用户，1=管理员
                .issuedAt(new Date())               // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(getKey())                 // 签名
                .compact();                          // 压缩成字符串
    }

    /**
     * 解析 JWT token，返回 payload 里的所有声明
     * 签名不对或已过期会抛异常，由调用方捕获
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getKey())       // 用同一个密钥验签
                .build()
                .parseSignedClaims(token)   // 解析签名 token
                .getPayload();              // 取出 payload
    }
}