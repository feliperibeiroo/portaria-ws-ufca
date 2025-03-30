package com.portaria.portaria_ws.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaUpdateRequest {

    private Timestamp horFinal;
    private Boolean autorizada;

}
