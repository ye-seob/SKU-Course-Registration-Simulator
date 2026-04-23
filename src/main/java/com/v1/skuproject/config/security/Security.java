package com.v1.skuproject.config.security;

import com.v1.skuproject.config.jwt.JwtAuthenticationFilter;
import com.v1.skuproject.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class Security {

    private final JwtProvider jwtProvider;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 로그인 & 회원가입 & Guest 로그인
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/guest",
                                "api/v1/simulation/**"
                        ).permitAll()

                        // Swagger & WS
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws/**",
                                "/app/**",
                                "/user/**",
                                "/topic/**",
                                "/queue/**"
                        ).permitAll()
                        // 평점은 USER만
                        .requestMatchers("/api/v1/lectures/*/rating").hasRole("USER")

                        // 강의 조회는 누구나
                        .requestMatchers("/api/v1/lectures/**").permitAll()

                        // 장바구니는 USER만
                        .requestMatchers("/api/v1/cart/add").hasRole("USER")
                        .requestMatchers("/api/v1/cart/delete/**").hasRole("USER")

                        // 수강신청은 로그인만 하면 가능
                        .requestMatchers("/api/v1/enrollments/**").authenticated()

                        .requestMatchers("/api/v1/practice/ranking").permitAll()  // 랭킹은 누구나 조회
                        .requestMatchers("/api/v1/practice/**").authenticated()   // 나머지는 로그인 필요


                        // 나머지
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}