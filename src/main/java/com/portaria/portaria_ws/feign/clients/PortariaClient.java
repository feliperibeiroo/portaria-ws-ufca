package com.portaria.portaria_ws.feign.clients;

import com.portaria.portaria_ws.dto.request.UserFeignCreateRequest;
import com.portaria.portaria_ws.dto.request.UserFeignUpdateRequest;
import com.portaria.portaria_ws.dto.response.UserFeignResponse;
import com.portaria.portaria_ws.feign.PortariaOAuthFeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "portaria-client",
    url = "${admin-portaria.url}",
    configuration = PortariaOAuthFeignInterceptor.class)
public interface PortariaClient {

    @PostMapping("users")
    ResponseEntity createUser(@RequestBody UserFeignCreateRequest createRequest);

    @PutMapping("users/{id_usuario}")
    ResponseEntity updateUser(@PathVariable("id_usuario") String idUsuario, @RequestBody UserFeignUpdateRequest userUpdateRequest);

    @GetMapping("users?username={username}&first={first}&max={max}")
    ResponseEntity<List<UserFeignResponse>> getUsuariosByUsername(
            @PathVariable("username") String username,
            @PathVariable("first") Long first,
            @PathVariable("max") Long max
    );

    @GetMapping("users?q=EMP_{id_empresa}:active&first={first}&max={max}&briefRepresentation={brief_rep}")
    List<UserFeignResponse> getUsuariosByEmpresa(
            @PathVariable("id_empresa") Long idEmpresa,
            @PathVariable("first") Long first,
            @PathVariable("max") Long max,
            @PathVariable("brief_rep") Boolean briefRepresentation
    );

    @GetMapping("users/count?q=EMP_{id_empresa}:active")
    Long getUsuariosByEmpresaCount(
            @PathVariable("id_empresa") Long idEmpresa
    );

}
