package ua.habatynchik.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.habatynchik.userservice.model.Role;
import ua.habatynchik.userservice.model.User;

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

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.secondName = user.getSecondName();
        this.role = RoleEnum.valueOf(user.getRole().getRoleEnum().name());
    }
}
