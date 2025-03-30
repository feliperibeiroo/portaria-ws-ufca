package com.portaria.portaria_ws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CondominioResponse {

    private Long id;
    private String img;
    private String nome;
    private String local;

}
