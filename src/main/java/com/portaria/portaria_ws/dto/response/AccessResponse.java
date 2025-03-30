package com.portaria.portaria_ws.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessResponse {

    private String qrCode;

    @NotNull
    private String cpf;

    @NotNull
    private String nome;

    @NotNull
    private Boolean isAcompanhante;

}