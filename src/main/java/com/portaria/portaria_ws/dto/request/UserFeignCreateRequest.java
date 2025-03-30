package com.portaria.portaria_ws.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeignCreateRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, List<String>> attributes;

}