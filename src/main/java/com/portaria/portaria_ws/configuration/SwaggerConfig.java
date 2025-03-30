package com.portaria.portaria_ws.configuration;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.openapi.base-url}")
    private String baseUrl;

    @Value("${swagger.openapi.token-url}")
    private String tokenUrl;

    @Value("${swagger.openapi.authorization-url}")
    private String authorizationUrl;

    @Bean
    public OpenAPI myOpenAPI() {

        Server prodServer = new Server();
        prodServer.setUrl(baseUrl);
        prodServer.setDescription("URL da aplicação");

        Contact contact = new Contact();
        contact.setEmail("felipebezerra35@gmail.com");
        contact.setName("Felipe Ribeiro Bezerra");

        Info info = new Info()
            .title("App Portaria")
            .version("0.0.1-SNAPSHOT")
            .contact(contact)
            .description("Backend do App Portaria");

        return new OpenAPI()
            .info(info)
            .servers(List.of(prodServer))
            .addSecurityItem(new SecurityRequirement().addList("sso-portaria"))
            .components(new Components()
                .addSecuritySchemes("sso-portaria", createOAuthScheme())
            );
    }

    private SecurityScheme createOAuthScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(createOAuthFlows());
    }

    private OAuthFlows createOAuthFlows() {
        final var oauthFlow = new OAuthFlow()
            .authorizationUrl(authorizationUrl)
            .refreshUrl(tokenUrl)
            .tokenUrl(tokenUrl)
            .scopes(new Scopes());
        return new OAuthFlows().implicit(oauthFlow);
    }
}
