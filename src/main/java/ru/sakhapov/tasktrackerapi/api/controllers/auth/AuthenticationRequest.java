package ru.sakhapov.tasktrackerapi.api.controllers.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NotBlank(message = "Username can't be empty.")
    @Size(min = 3, max = 30)
    String username;

    @NotBlank(message = "Password can't be empty.")
    @Size(min = 6, max = 30)
    String password;

}