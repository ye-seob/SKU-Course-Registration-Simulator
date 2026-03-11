package com.v1.skuproject.config.jwt;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
public class JwtProvider {

    private final Key key;
    private final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60 * 1; // 1시간

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {

        byte[] decoded = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    /**
      Access Token 생성
     */
    public String generateToken(Long userId, String studentId,Role role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("studentId", studentId)
                .claim("role",role)
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
            return body.get("userId", Long.class); // 없으면 null 반환
        } catch (JwtException e) {
            throw new BaseException(ErrorCode.TOKEN_INVALID);
        }
    }

    /**
       jwt에서 role 얻기
     */
    public Role getRole(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String roleValue = body.get("role", String.class);
            return Role.valueOf(roleValue);

        } catch (JwtException e) {
            throw new BaseException(ErrorCode.TOKEN_INVALID);
        }
    }

    public Authentication getAuthentication(String token) {

        Role role = getRole(token);
        Long userId = getUserId(token);

        UserPrincipal principal = new UserPrincipal(userId, role);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority(role.name()))
        );
    }
}