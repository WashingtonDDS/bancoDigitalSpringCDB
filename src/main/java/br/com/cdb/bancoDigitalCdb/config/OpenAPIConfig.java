package br.com.cdb.bancoDigitalCdb.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Banco Digital Codigo de Base")
                        .version("v1.0")
                        .description("Documentação da minha API usando Springdoc OpenAPI")
                        .contact(new Contact()
                                .name("Washington Luiz Alves Da Silva")
                                .email("washington.dds@hotmail.com")
                        )
                );
    }
}

