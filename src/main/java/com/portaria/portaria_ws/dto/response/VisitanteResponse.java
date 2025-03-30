package com.portaria.portaria_ws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitanteResponse {

    private Long id;
    private String cpf;
    private String nome;

}
