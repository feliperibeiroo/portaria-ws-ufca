package com.portaria.portaria_ws.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaCreateRequest {

    @NotNull
    private Long idEmpresa;
    private String anfitriao;
    private String motivo;

    @NotNull
    private List<AccessRequest> acessos;

}