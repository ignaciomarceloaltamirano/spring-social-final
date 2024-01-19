package com.example.demo.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    @NotEmpty(message = "Email must not be empty.")
    @Email
    @Size(min = 12, max = 80, message = "Size must be between 12 and 80 characters long.")
    private String email;
    @NotEmpty(message = "Password must not be empty.")
    @Size(min = 3, max = 80, message = "Size must be between 3 and 80 characters long.")
    private String password;
}
