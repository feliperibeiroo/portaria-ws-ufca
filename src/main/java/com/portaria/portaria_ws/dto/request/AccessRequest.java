package com.portaria.portaria_ws.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {

    @NotNull
    private String cpf;

    @NotNull
    private String nome;

    @NotNull
    private Boolean isAcompanhante;

}