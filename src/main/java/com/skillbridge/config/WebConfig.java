package com.skillbridge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Expose the uploads directory so images are served correctly without server restart
        String dirName = "src/main/resources/static/uploads";
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }
        
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:/" + uploadPath + "/");
    }
}
