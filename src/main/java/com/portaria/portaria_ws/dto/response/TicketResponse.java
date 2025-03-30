package com.portaria.portaria_ws.dto.response;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {

    private Long id;
    private String token;
    private String username;

    @JsonProperty("creation_time")
    private Timestamp creationTime;

    @JsonProperty("expiration_time")
    private Timestamp expirationTime;

}