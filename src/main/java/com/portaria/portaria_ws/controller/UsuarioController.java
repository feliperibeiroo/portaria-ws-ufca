package com.portaria.portaria_ws.controller;

import com.portaria.portaria_ws.dto.request.UserCreateRequest;
import com.portaria.portaria_ws.dto.response.TicketResponse;
import com.portaria.portaria_ws.dto.response.UserFeignResponse;
import com.portaria.portaria_ws.dto.response.UserResponse;
import com.portaria.portaria_ws.dto.request.UserUpdateRequest;
import com.portaria.portaria_ws.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("usuario")
@Tag(name = "Usuários", description = "Endpoints relativos ao controle de usuários")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("empresa/{id_empresa}")
    @Operation(summary = "Cadastra um novo usuário em uma empresa")
    public ResponseEntity<Void> createUsuario(@PathVariable("id_empresa") Long idEmpresa, @RequestBody UserCreateRequest userCreateRequest) {
        return usuarioService.cadastrarUsuario(idEmpresa, userCreateRequest);
    }

    @PutMapping("empresa/{id_empresa}/cpf/{cpf}")
    @Operation(summary = "Atualiza informações do usuário")
    public ResponseEntity<Void> updateUsuario(
            @RequestBody UserUpdateRequest userUpdateRequest,
            @PathVariable("id_empresa") Long idEmpresa,
            @PathVariable("cpf") String cpf
    ) {
        return usuarioService.updateUsuario(idEmpresa, cpf, userUpdateRequest);
    }

    @GetMapping("empresa/{id_empresa}")
    @Operation(summary = "Obter os usuários por empresa")
    public PageImpl<UserResponse> getUsuariosByEmpresa(
        @PathVariable("id_empresa") Long idEmpresa,
        @RequestParam(value = "first", defaultValue = "0") Long first,
        @RequestParam(value = "limit", defaultValue = "20") Long limit,
        @RequestParam(value = "briefRepresentation", defaultValue = "false") Boolean briefRepresentation
    ) {
        return usuarioService.getUsuariosByEmpresa(idEmpresa, first, limit, briefRepresentation);
    }

    @GetMapping("cpf/{cpf}")
    @Operation(summary = "Obter o usuário pelo CPF")
    public ResponseEntity<UserResponse> getUsuarioByCpf(
        @PathVariable("cpf") String cpf
    ) {
        UserFeignResponse feignResponse = usuarioService.getUserByUsername(cpf);
        if (feignResponse==null) {
            return ResponseEntity.notFound().build();
        }
        UserResponse response = modelMapper.map(feignResponse, UserResponse.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("empresa/{id_empresa}/qrcode")
    @Operation(summary = "Obter um novo QR Code dinâmico para o usuário")
    public ResponseEntity<TicketResponse> getQrCodeUsuario(
        final JwtAuthenticationToken auth,
        @PathVariable("id_empresa") Long idEmpresa,
        @PathVariable("hor_final") Timestamp horFinal
    ) {
        String userId = (String) auth.getTokenAttributes().get("preferred_username");
        return usuarioService.createQrCodeByIdUsuarioAndIdEmpresa(userId, idEmpresa, horFinal);
    }

    @DeleteMapping("empresa/{id_empresa}/cpf/{cpf}")
    @Operation(summary = "Remove/desvincula o usuário de uma empresa")
    public ResponseEntity<Void> deleteUsuario(
            @PathVariable("id_empresa") Long idEmpresa,
            @PathVariable("cpf") String cpf
    ) {
        return usuarioService.desvincularUsuarioEmpresa(idEmpresa, cpf);
    }

}