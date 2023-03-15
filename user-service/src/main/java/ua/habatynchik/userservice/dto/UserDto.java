package ua.habatynchik.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.habatynchik.userservice.model.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String username;

    private String email;

    private String firstName;

    private String secondName;
    private RoleEnum role;

    public enum RoleEnum {
        CLIENT, ADMIN, GUEST;
    }

}
