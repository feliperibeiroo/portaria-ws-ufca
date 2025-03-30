package com.portaria.portaria_ws.service;

import com.portaria.portaria_ws.dto.request.UserCreateRequest;
import com.portaria.portaria_ws.dto.request.UserFeignUpdateRequest;
import com.portaria.portaria_ws.dto.response.*;
import com.portaria.portaria_ws.feign.clients.AppCatracaClient;
import com.portaria.portaria_ws.feign.clients.PortariaClient;
import com.portaria.portaria_ws.dto.request.UserFeignCreateRequest;
import com.portaria.portaria_ws.dto.request.UserUpdateRequest;
import com.portaria.portaria_ws.models.enums.TicketType;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    PortariaClient portariaClient;

    @Autowired
    AppCatracaClient appCatracaClient;

    @Autowired
    ModelMapper modelMapper;

    public UserFeignResponse getUserByUsername(String username) {

        ResponseEntity<List<UserFeignResponse>> feignResponse =
                portariaClient.getUsuariosByUsername(username, 0L, 1L);
        List<UserFeignResponse> users;
        if (feignResponse.getStatusCode().is2xxSuccessful()) {
            users = feignResponse.getBody();

            if (!users.isEmpty()) return users.getFirst();
        }
        return null;

    }

    public ResponseEntity<Void> updateUsuario(Long idEmpresa, String username, UserUpdateRequest userUpdateRequest) {

        // Certificar se usuário já está no sistema
        UserFeignResponse fetchedUser = getUserByUsername(username);
        if (fetchedUser==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // Mapear mudanças para o usuário obtido
        modelMapper.map(userUpdateRequest, fetchedUser);

        // Mapear permissões
        Map<String, List<String>> attributtes;
        if (fetchedUser.getAttributes()==null)
            attributtes = new HashMap<>();
        else
            attributtes = fetchedUser.getAttributes();

        List<String> adminPermissions = new ArrayList<String>();
        try {
            adminPermissions = List.of(attributtes.get("ADMIN_PERMISSIONS").get(0).split(","));
        } catch (Exception e) {
            log.info("Não foi possível obter o atributo ADMIN_PERMISSIONS anterior");
        }

        List<String> newPermissions = userUpdateRequest.getPermissions().stream()
                .map(permission -> permission+":"+idEmpresa).toList();

        attributtes.put("ADMIN_PERMISSIONS",
                List.of(String.join(",", Stream.concat(
                                adminPermissions.stream(), newPermissions.stream()
                ).collect(Collectors.toSet()))
        ));

        // Mapear empresa
        attributtes.putIfAbsent("EMP_"+idEmpresa, List.of("active"));

        fetchedUser.setAttributes(attributtes);

        // Enviar alterações do novo usuário
        try {
            UserFeignUpdateRequest updatableUser = modelMapper.map(fetchedUser, UserFeignUpdateRequest.class);
            ResponseEntity response = portariaClient.updateUser(fetchedUser.getId(), updatableUser);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return response;
            }
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Void> cadastrarUsuario(Long idEmpresa, UserCreateRequest userCreateRequest) {

        UserFeignCreateRequest requestUser = new UserFeignCreateRequest();

        UserFeignResponse fetchedUser = getUserByUsername(userCreateRequest.getUsername());

        Map<String, List<String>> attributtes = new HashMap<>();

        // Caso tenha um usuário com o CPF enviado, mapear as propriedades dele
        if (fetchedUser!=null) {
            if (fetchedUser.getAttributes()!=null) {
                attributtes = fetchedUser.getAttributes();
            }
            modelMapper.map(fetchedUser, requestUser);
        }

        // Mapear as propriedades do DTO recebido (sobreescrevendo as anteriores, se for o caso)
        modelMapper.map(userCreateRequest, requestUser);

        // Mapear empresa
        attributtes.putIfAbsent("EMP_"+idEmpresa, List.of("active"));
        requestUser.setAttributes(attributtes);

        // Enviar novo usuário
        try {
            ResponseEntity response;
            if (fetchedUser==null) {
                response = portariaClient.createUser(requestUser);
            } else {
                UserFeignUpdateRequest userUpdateRequest = modelMapper.map(requestUser, UserFeignUpdateRequest.class);
                response = portariaClient.updateUser(fetchedUser.getId(), userUpdateRequest);
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return response;
            }
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public PageImpl<UserResponse> getUsuariosByEmpresa(Long idEmpresa, Long first, Long max, Boolean briefRepresentation) {

        PageRequest pageable = PageRequest.of(
                Integer.parseInt(String.valueOf(first/max)),
                Integer.parseInt(String.valueOf(max)));

        List<UserFeignResponse> users = portariaClient.getUsuariosByEmpresa(idEmpresa, first, max, briefRepresentation);
        Long totalUsers = portariaClient.getUsuariosByEmpresaCount(idEmpresa);

        List<UserResponse> responseUsers = users.stream()
                .map(u -> modelMapper.map(u, UserResponse.class))
                .toList();

        return new PageImpl<UserResponse>(responseUsers, pageable, totalUsers);
    }

/*
    public ResponseEntity<UserQrCodeResponse> getTicketByIdUsuarioAndIdEmpresa(Long idEmpresa) {
        return ;
    }
*/

    public ResponseEntity<TicketResponse> createQrCodeByIdUsuarioAndIdEmpresa(String userId, Long idEmpresa, Timestamp horFinal) {
        return appCatracaClient.createNewTicket(
            idEmpresa,
            userId,
            horFinal,
            "app-portaria",
            "1.0.0",
            TicketType.DINAMICO
        );
    }

    public ResponseEntity<Void> desvincularUsuarioEmpresa(Long idEmpresa, String username) {

        // Certificar se usuário já está no sistema
        ResponseEntity<List<UserFeignResponse>> feignResponse = portariaClient.getUsuariosByUsername(username, 0L, 1L);
        List<UserFeignResponse> users;
        if (feignResponse.getStatusCode().is2xxSuccessful()) {
            users = feignResponse.getBody();
        } else {
            return ResponseEntity.internalServerError().build();
        }

        // Obter usuário
        if (users.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        UserFeignResponse requestUser = users.getFirst();

        // Obter atributos do usuário e remover o atributo EMP_{id_empresa}
        Map<String, List<String>> attributtes = requestUser.getAttributes();
        attributtes.remove("EMP_"+idEmpresa);
        requestUser.setAttributes(attributtes);

        // Enviar alterações do novo usuário
        try {
            UserFeignUpdateRequest userUpdateRequest = modelMapper.map(requestUser, UserFeignUpdateRequest.class);
            ResponseEntity response = portariaClient.updateUser(requestUser.getId(), userUpdateRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().build();
            } else {
                return response;
            }
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
