package com.portaria.portaria_ws.dto.request;

import com.portaria.portaria_ws.models.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private List<Permission> permissions;

}
