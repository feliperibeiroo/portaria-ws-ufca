package com.portaria.portaria_ws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaResponse {

    private Long id;
    private Long idEmpresa;
    private Timestamp horSolicitacao;
    private Timestamp horInicio;
    private Timestamp horFinal;
    private String motivo;
    private String anfitriao;
    private String idAtendente;
    private Boolean autorizada;
    private Timestamp horAtendimento;
    private List<AccessResponse> acessos;

}