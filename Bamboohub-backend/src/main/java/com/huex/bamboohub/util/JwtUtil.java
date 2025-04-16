package com.huex.bamboohub.util;

import io.jsonwebtoken.*;

import java.time.Instant;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Date;
import java.time.LocalDateTime;
import com.huex.bamboohub.dao.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {
    @Autowired
    private UserRepo userRepo;

    private final String signature="signature";

    public String generateToken(Long userId,String username) {

        // 使用 Instant 来确保使用 UTC 时间计算
        Instant expirationInstant = Instant.now().plus(30, ChronoUnit.DAYS);  // 30天后
        Date expirationDate = Date.from(expirationInstant);  // 转换为 Date 对象

        // 生成 JWT token
        JwtBuilder jwtBuilder=Jwts.builder();
        String token=jwtBuilder
            //header
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg","HS256")
            //payload
            .claim("userId",userId)
            .claim("username",username)
            .setSubject("Authentication")
            .setExpiration(expirationDate)
            .setId(UUID.randomUUID().toString())
            //signature
            .signWith(SignatureAlgorithm.HS256,signature)
            .compact();
        return token;
    }

    public Claims parseToken(String token) {
        JwtParser jwtParser=Jwts.parser();
        Jws<Claims> claimsJws=jwtParser.setSigningKey(signature).parseClaimsJws(token);
        Claims claims=claimsJws.getBody();
        Long userId=claims.get("userId",Long.class);
        String username=claims.get("username",String.class);
        String uuid=claims.getId();
        String subject=claims.getSubject();
        Date expiration=claims.getExpiration();
        System.out.println("Expiration Date: "+expiration);
        return claims;
    }

    public User parseUser(String token) {
        if (!StringUtils.hasText(token)) return null;
        Claims claims=parseToken(token);
        Long userId=claims.get("userId",Long.class);
        String username=claims.get("username",String.class);
        User user=userRepo.findByIdAndUsername(userId,username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        return user;
    }

    public Long parseUserId(String token) {
        if (!StringUtils.hasText(token)) return null;
        Claims claims=parseToken(token);
        Long userId=claims.get("userId",Long.class);
        return userId;
    }

    public String parseUsername(String token) {
        if (!StringUtils.hasText(token)) return null;
        Claims claims=parseToken(token);
        String username=claims.get("username",String.class);
        return username;
    }
}
