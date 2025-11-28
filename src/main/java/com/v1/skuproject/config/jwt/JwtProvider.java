package com.v1.skuproject.config.jwt;

import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtProvider {

    private final Key key;
    private final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60 * 3; // 3시간

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {

        byte[] decoded = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    /**
       Access Token 생성
     */
    public String generateToken(Long userId, int studentId) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("studentId", studentId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
      JWT 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException e) {
            // 서명 오류, 만료, 변조 등 모든 예외 처리
            throw new BaseException(ErrorCode.TOKEN_INVALID);
        }
    }

    /**
       jwt에서 userId studentId 얻기
     */
    public Long getUserId(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return body.get("userId", Long.class);
        } catch (JwtException e) {
            throw new BaseException(ErrorCode.TOKEN_INVALID);
        }
    }

    public int getUserStudentId(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return body.get("studentId", Integer.class);
        } catch (JwtException e) {
            throw new BaseException(ErrorCode.TOKEN_INVALID);
        }
    }
}