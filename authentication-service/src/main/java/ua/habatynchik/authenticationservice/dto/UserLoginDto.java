package ua.habatynchik.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
