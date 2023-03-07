package ua.habatynchik.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class UserDto {
    private String username;
    private String email;
    private String firstName;
    private String secondName;
    private String password;
    private String passwordCopy;

}
