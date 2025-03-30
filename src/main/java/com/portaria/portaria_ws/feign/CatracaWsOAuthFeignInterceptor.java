package com.portaria.portaria_ws.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import org.springframework.stereotype.Component;

@Component
public class CatracaWsOAuthFeignInterceptor implements RequestInterceptor {

    public static final String CLIENT_REGISTRATION_ID = "sso-catraca-ws";

    OAuth2AuthorizedClientManager authorizedClientManager;

    public CatracaWsOAuthFeignInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(CLIENT_REGISTRATION_ID)
                .principal("client")
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            throw new IllegalStateException("Falha ao obter token de acesso OAuth2.");
        }

        requestTemplate.header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue());
    }
}