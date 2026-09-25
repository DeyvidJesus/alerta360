package com.alerta360.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI alerta360OpenApi() {
        return new OpenAPI().info(new Info()
                .title("Alerta360 API")
                .description("API REST para monitoramento de sensores IoT: cadastro de sensores, "
                        + "ingestão de leituras, geração automática de alertas e dashboard.")
                .version("0.0.1")
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
