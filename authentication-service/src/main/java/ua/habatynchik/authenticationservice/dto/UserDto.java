package ua.habatynchik.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Data
public class UserDto {

    @NotBlank
    @Size(min = 4, max = 64, message = "sizeOutOfBounds")
    private String username;

    @NotBlank
    @Size(min = 4, max = 64, message = "sizeOutOfBounds")
    @Pattern(regexp = RegExp.EMAIL, message = "emailNotMatchTemplate")
    private String email;

    @NotBlank
    @Size(max = 32)
    private String firstName;

    @NotBlank
    @Size(max = 32)
    private String secondName;

    @NotBlank
    @Size(min = 8, max = 64, message = "sizeOutOfBounds")
    @Pattern(regexp = RegExp.PASSWORD, message = "passwordNotMatchTemplate")
    private String password;

    @NotBlank
    private String passwordCopy;

}
