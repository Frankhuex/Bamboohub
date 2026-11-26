package com.huex.bamboohub.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 统一为后端业务 Controller 挂载 /api 前缀，把根路径 / 留给前端静态页面托管
        configurer.addPathPrefix("/api",
            HandlerTypePredicate.forAnnotation(RestController.class)
                .and(HandlerTypePredicate.forBasePackage("com.huex.bamboohub.controller")));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 托管前端构建产物，并对 React Router 客户端路由进行 index.html 回退
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    if (resourcePath.startsWith("api/") || resourcePath.equals("api")
                            || resourcePath.startsWith("actuator/") || resourcePath.equals("actuator")) {
                        return null;
                    }
                    if (StringUtils.hasText(resourcePath) && !resourcePath.endsWith("/")) {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                    }
                    Resource indexHtml = location.createRelative("index.html");
                    return (indexHtml.exists() && indexHtml.isReadable()) ? indexHtml : null;
                }
            });
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
