package com.huex.bamboohub.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 配置全局CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有来源访问所有接口
        registry.addMapping("/**")  // 配置所有路径
            .allowedOriginPatterns("*")  // 允许所有来源
            .allowedMethods("*")  // 允许的HTTP请求方法
            .allowedHeaders("*")  // 允许的请求头
            .allowCredentials(true);  // 是否允许携带凭证（如Cookies）
    }
}