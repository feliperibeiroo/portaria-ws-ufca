package com.portaria.portaria_ws.controller;

import com.portaria.portaria_ws.dto.request.VisitaUpdateRequest;
import com.portaria.portaria_ws.dto.response.ActivityFeignResponse;
import com.portaria.portaria_ws.dto.response.VisitaResponse;
import com.portaria.portaria_ws.service.VisitanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("visitante")
@Tag(name = "Visitantes", description = "Endpoints relativos ao controle de visitantes")
public class VisitanteController {

    @Autowired
    VisitanteService visitanteService;

    @GetMapping("visita/empresa/{id_empresa}")
    @Operation(summary = "Obter visitas por empresa")
    public PageImpl<VisitaResponse> getVisitasByEmpresa(
            @PathVariable("id_empresa") Long idEmpresa,
            @RequestParam(value = "first", defaultValue = "0") Long first,
            @RequestParam(value = "max", defaultValue = "10") Long max
    ) {
        return visitanteService.getVisitasByEmpresa(idEmpresa, first, max);
    }

    @GetMapping("visita/{id_visita}/empresa/{id_empresa}/aprovar")
    @Operation(summary = "Aprovar a solicitação de visita")
    public ResponseEntity<String> aprovarVisita(
        final JwtAuthenticationToken auth,
        @PathVariable("id_visita") Long idVisita,
        @PathVariable("id_empresa") Long idEmpresa,
        @RequestParam("hor_final")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        final LocalDateTime horFinal,
        @RequestParam("aprovado") Boolean aprovado
    ) {
        String idAtendente = (String) auth.getTokenAttributes().get("sid");
        return visitanteService.aprovarReprovarVisita(idEmpresa, idAtendente, idVisita, Timestamp.valueOf(horFinal), aprovado);
    }

    @GetMapping("visita/{id_visita}/empresa/{id_empresa}/prolongar")
    @Operation(summary = "Prolongar a visita")
    public ResponseEntity<String> encerrarVisita(
        @PathVariable("id_visita") Long idVisita,
        @PathVariable("id_empresa") Long idEmpresa,
        @RequestParam("hor_final")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        final LocalDateTime horFinal
    ) {
        VisitaUpdateRequest visitaUpdateRequest = new VisitaUpdateRequest();
        visitaUpdateRequest.setHorFinal(new Timestamp(System.currentTimeMillis()));
        return visitanteService.prolongarVisita(idEmpresa, idVisita, Timestamp.valueOf(horFinal));
    }

    @GetMapping("visita/{id_visita}/empresa/{id_empresa}/encerrar")
    @Operation(summary = "Encerrar a visita")
    public ResponseEntity<String> encerrarVisita(
            @PathVariable("id_visita") Long idVisita,
            @PathVariable("id_empresa") Long idEmpresa
    ) {
        VisitaUpdateRequest visitaUpdateRequest = new VisitaUpdateRequest();
        visitaUpdateRequest.setHorFinal(new Timestamp(System.currentTimeMillis()));
        return visitanteService.encerrarVisita(idEmpresa, idVisita);
    }

    @GetMapping("{id_visitante}/visita/{id_visita}/empresa/{id_empresa}/historico")
    @Operation(summary = "Obter o histórico de um visitante para uma visita")
    public ResponseEntity<List<ActivityFeignResponse>> getHistoricoVisitante(
        @PathVariable("id_visita") Long idVisita,
        @PathVariable("id_empresa") Long idEmpresa,
        @PathVariable("id_visitante") Long idVisitante,
        @RequestParam(value = "max", defaultValue = "10") Long limit
    ) {
        return visitanteService.getHistoricoVisitanteByVisita(idVisita, idVisitante, limit);
    }

}
