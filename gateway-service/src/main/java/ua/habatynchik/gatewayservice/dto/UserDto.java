package ua.habatynchik.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String secondName;

    private RoleEnum role;

    public enum RoleEnum {
        CLIENT, ADMIN, GUEST;
    }
}
