package br.com.cdb.bancoDigitalCdb.doc;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bando Digital Codigo de Base")
                        .version("v1.0")
                        .description("Documentação da minha API usando Springdoc OpenAPI")
                        .contact(new Contact()
                                .name("Washington Luiz Alves Da Silva")
                                .email("washington.dds@hotmail.com")
                        )
                );
    }
}

