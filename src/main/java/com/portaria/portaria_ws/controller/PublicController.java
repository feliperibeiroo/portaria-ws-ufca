package com.portaria.portaria_ws.controller;

import com.portaria.portaria_ws.dto.request.VisitaCreateRequest;
import com.portaria.portaria_ws.dto.response.CondominioResponse;
import com.portaria.portaria_ws.dto.response.EmpresaResponse;
import com.portaria.portaria_ws.dto.response.VisitaResponse;
import com.portaria.portaria_ws.dto.response.VisitanteResponse;
import com.portaria.portaria_ws.service.PublicService;
import com.portaria.portaria_ws.service.VisitanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public")
@Tag(name = "Public", description = "Endpoints públicos")
public class PublicController {

    @Autowired
    PublicService publicService;

    @Autowired
    VisitanteService visitanteService;

    @GetMapping("condominios")
    @Operation(summary = "Obter condomínios")
    public ResponseEntity<List<CondominioResponse>> getCondominios() {
        return publicService.getCondominios();
    }

    @GetMapping("condominios/{id_condominio}")
    @Operation(summary = "Obter condomínio por ID")
    public ResponseEntity<CondominioResponse> getCondominioById(
        @PathVariable("id_condominio") Long idCondominio
    ) {
        return publicService.getCondominioById(idCondominio);
    }

    @GetMapping("condominios/{id_condominio}/empresas")
    @Operation(summary = "Obter empresas de um condomínio")
    public ResponseEntity<List<EmpresaResponse>> getEmpresasByCondominio(
        @PathVariable("id_condominio") Long idCondominio
    ) {
        return publicService.getEmpresasByCondominio(idCondominio);
    }

    @GetMapping("empresa/{id_empresa}")
    @Operation(summary = "Obter empresa por ID")
    public ResponseEntity<EmpresaResponse> getEmpresaById(
        @PathVariable("id_empresa") Long idEmpresa
    ) {
        return publicService.getEmpresaById(idEmpresa);
    }

    @PostMapping("visita")
    @Operation(summary = "Cria uma nova visita")
    public ResponseEntity<Void> createVisita(@Validated @RequestBody VisitaCreateRequest visitaCreateRequest) {
        return visitanteService.createVisita(visitaCreateRequest);
    }

    @GetMapping("cpf/{cpf}")
    @Operation(summary = "Obter um visitante pelo CPF")
    public ResponseEntity<VisitanteResponse> getVisitanteByCpf(@PathVariable("cpf") String cpf) {
        return visitanteService.getVisitanteByUsername(cpf);
    }

    @GetMapping("visita/{id}")
    @Operation(summary = "Obter visita pelo ID")
    public ResponseEntity<VisitaResponse> getVisitaById(
            @PathVariable("id") Long id
    ) {
        return visitanteService.getVisitaById(id);
    }

    @GetMapping("visita/cpf/{cpf}")
    @Operation(summary = "Obter visita atual por CPF")
    public ResponseEntity<VisitaResponse> getCurrentVisitaByCpf(
        @PathVariable("cpf") String cpf
    ) {
        return visitanteService.getVisitaAtual(cpf);
    }

}
