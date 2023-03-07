package ua.habatynchik.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
