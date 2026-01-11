package com.v1.skuproject.config.jwt;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
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
            System.out.println(e.toString());
            return false;
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

    public Authentication getAuthentication(String token) {
        // 토큰에서 userId 추출
        Long userId = getUserId(token);

        // HTTP Filter와 동일하게 Principal에 userId(Long)를 넣음
        // 권한(Role)이 필요하다면 List.of() 대신 실제 권한 리스트 주입 필요
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

}