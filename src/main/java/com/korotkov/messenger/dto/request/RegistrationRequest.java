package com.korotkov.messenger.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {
    @NotEmpty
    String name;

    @NotEmpty
    String surname;

    @Email
    String email;
    @Size(min = 1, max = 30, message = "your login size should be in range(1,30)")
    @NotEmpty
    String login;

    @Size(min = 3, max = 30, message = "your password size should be in range(3,30)")
    @NotEmpty
    String password;


}
