package com.huex.bamboohub.config;

import com.huex.bamboohub.service.BookService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {
    @Bean
    public ToolCallbackProvider toolCallbackProvider(BookService bookService) {
        return MethodToolCallbackProvider.builder().toolObjects(bookService).build();
    }
}
