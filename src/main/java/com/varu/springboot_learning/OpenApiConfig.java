

package com.varu.springboot_learning;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentApiDocumentation() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management API")
                        .description("REST API for managing students and courses")
                        .version("1.0.0"));
    }
}
