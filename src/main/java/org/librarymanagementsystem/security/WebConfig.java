package org.librarymanagementsystem.security;

import org.librarymanagementsystem.interceptors.LogsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LogsInterceptor logsInterceptor;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = Arrays.asList(frontendUrl.split(",")); // Allow multiple origins

        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0])) // Set origins dynamically
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Restrict to necessary methods
                .allowedHeaders("Authorization", "Content-Type") // Avoid wildcard '*'
                .allowCredentials(true) // Allow credentials (only when origins are explicitly set)
                .maxAge(3600); // Cache pre-flight response for 1 hour
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logsInterceptor)
                .addPathPatterns("/v1/api/auth/**");
              //  .excludePathPatterns("/api/auth/public/**");
    }

}
