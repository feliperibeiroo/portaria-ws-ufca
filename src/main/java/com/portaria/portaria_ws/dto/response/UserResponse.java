package com.portaria.portaria_ws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String emailVerified;
    private Timestamp createdTimestamp;
    private Map<String, List<String>> attributes;

}
