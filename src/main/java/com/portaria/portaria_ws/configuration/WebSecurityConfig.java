package com.portaria.portaria_ws.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@SuppressWarnings("removal")
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter =
            // Using the delegating converter multiple converters can be combined
            new DelegatingJwtGrantedAuthoritiesConverter(
                // First add the default converter
                new JwtGrantedAuthoritiesConverter(),
                // Second add our custom Keycloak specific converter
                new JwtRolesConverter());

        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.authorizeHttpRequests(authz -> authz
            //.requestMatchers(mvcMatcherBuilder.pattern("/xml/**")).authenticated()
            .anyRequest().permitAll()
        );
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(
            jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt)));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors().and().csrf().disable();
        return http.build();
    }

}