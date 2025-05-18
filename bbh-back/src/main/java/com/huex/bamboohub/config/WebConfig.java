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
            .allowedOrigins(
                    "http://localhost:5173",
                    "http://123.60.152.98:5173",
                    "http://115.159.31.68:5173",
                    "https://bbh.beforce.site:5173",
                    "http://bbh.beforce.site:5173",
                    "http://bbh.beforce.site",
                    "https://bbh.beforce.site",
                    "https://115.159.31.68",
                    System.getenv("FRONTEND_URL")
            )  // 允许来自前端的请求源（根据你的前端地址修改）
            .allowedMethods("*")  // 允许的HTTP请求方法
            .allowedHeaders("*")  // 允许的请求头
            .allowCredentials(true);  // 是否允许携带凭证（如Cookies）
    }
}