package org.librarymanagementsystem.security;

import org.librarymanagementsystem.interceptors.LogsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

   /* @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = Arrays.asList(frontendUrl.split(",")); // Allow multiple origins

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Set origins dynamically
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Restrict to necessary methods
                .allowedHeaders("Authorization", "Content-Type") // Avoid wildcard '*'
                .allowCredentials(true); // Allow credentials (only when origins are explicitly set)
         //   .maxAge(3600); // Cache pre-flight response for 1 hour
    }*/

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // or use List.of(frontendUrl) if injected
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // Required if using cookies or Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logsInterceptor)
                .addPathPatterns("/v1/api/auth/**");
              //  .excludePathPatterns("/api/auth/public/**");
    }

}
