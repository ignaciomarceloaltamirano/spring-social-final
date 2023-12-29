package com.example.demo.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    @NotEmpty(message = "Username must not be empty.")
    @Size(min = 3, max = 40, message = "Size must be between 3 and 40 characters long.")
    private String username;
    @NotEmpty(message = "Email must not be empty.")
    @Size(min = 3, max = 80, message = "Size must be between 3 and 80 characters long.")
    private String email;
    @NotEmpty(message = "Password must not be empty.")
    @Size(min = 3, max = 80, message = "Size must be between 3 and 80 characters long.")
    private String password;
}
