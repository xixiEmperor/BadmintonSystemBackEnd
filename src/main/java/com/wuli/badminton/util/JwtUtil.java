package com.wuli.badminton.util;

import com.wuli.badminton.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Autowired(required = false)
    private TokenBlacklistService tokenBlacklistService;

    /**
     * 根据认证信息生成JWT令牌
     */
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从令牌中提取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    
    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }
    
    /**
     * 计算令牌的剩余有效时间（毫秒） 可用来刷新token有效时间
     */
    public long getRemainingTimeFromToken(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * 验证令牌
     * 检查签名是否有效，是否过期，以及是否在黑名单中
     */
    public boolean validateToken(String token) {
        try {
            // 检查令牌签名和有效期
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            
            // 如果已配置黑名单服务，则检查令牌是否在黑名单中
            if (tokenBlacklistService != null && tokenBlacklistService.isBlacklisted(token))
                return false;

            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 将令牌添加到黑名单
     * 用于用户退出登录时使当前令牌失效
     */
    public void invalidateToken(String token) {
        if (tokenBlacklistService != null) {
            tokenBlacklistService.addToBlacklist(token);
        }
    }
} 