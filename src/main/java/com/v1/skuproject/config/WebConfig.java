package com.v1.skuproject.config;

import com.v1.skuproject.util.traceId.TraceIdFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebConfig {

    @Bean
    public Filter traceIdFilter() {
        return new TraceIdFilter();
        //  모든 HTTP 요청이 컨트롤러에 도달하기 전에 먼저 실행
    }
}
