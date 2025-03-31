package org.librarymanagementsystem.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("API documentation for the Library Management System")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .contact(new Contact()
                                .email("mahitoshgiri287@mail.com")
                                .name("Mahitosh Giri")
                                .url("https://www.url.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Library Management System Wiki")
                        .url("https://springshop.wiki.github.org/docs"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    //    @Bean
    //    public GroupedOpenApi publicApi() {
    //        return GroupedOpenApi.builder()
    //                .group("public api")
    //                .pathsToMatch("/api/public/**") // Include specific endpoints
    //                .build();
    //    }

    //
    //    @Bean
    //    public GroupedOpenApi adminApi() {
    //        return GroupedOpenApi.builder()
    //                .group("admin")
    //                .pathsToMatch("/api/**") // Admin endpoints only
    //                .build();
    //    }

}
