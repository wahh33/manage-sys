package com.web_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    @Value("${web.uploadPath.img}")
    private String path;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/img/**")
                .addResourceLocations("file:/"+path);                                     
    }
}
