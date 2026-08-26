package com.software.knowledgehub.security.service;

import com.software.knowledgehub.security.config.JwtProperties;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import com.software.knowledgehub.system.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;

    /**
     * 为登录用户创建 JWT 并保存登录状态。
     */
    public String createToken(SysUser user) {
        String tokenId = UUID.randomUUID().toString();
        Date expirationTime = new Date(System.currentTimeMillis() + jwtProperties.getExpiration());

        // 生成携带用户身份和唯一登录标识的 JWT。
        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .id(tokenId)
                .issuedAt(new Date())
                .expiration(expirationTime)
                .signWith(getSecretKey())
                .compact();

        // Redis 记录当前 Token 的有效状态，TTL 与 JWT 保持一致。
        stringRedisTemplate.opsForValue().set(
                getRedisKey(tokenId),
                user.getId().toString(),
                jwtProperties.getExpiration(),
                TimeUnit.MILLISECONDS
        );
        return token;
    }

    /**
     * 解析 JWT 中保存的登录身份。
     */
    public AuthenticatedUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new AuthenticatedUser(
                claims.get("userId", Long.class),
                claims.getSubject(),
                claims.getId()
        );
    }

    /**
     * 检查 Token 的 Redis 登录状态是否存在。
     */
    public boolean isTokenActive(String tokenId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(getRedisKey(tokenId)));
    }

    /**
     * 删除当前 Token 的 Redis 登录状态。
     */
    public void deleteToken(String tokenId) {
        stringRedisTemplate.delete(getRedisKey(tokenId));
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String getRedisKey(String tokenId) {
        return "auth:token:" + tokenId;
    }
}
