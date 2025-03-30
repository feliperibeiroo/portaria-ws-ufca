package com.portaria.portaria_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableFeignClients
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableMethodSecurity
public class PortariaWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortariaWsApplication.class, args);
	}

	// TODO Configurar as permissões dos usuários via Keycloak

}
