package ua.habatynchik.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
