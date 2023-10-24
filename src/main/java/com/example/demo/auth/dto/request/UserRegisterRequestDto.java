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
    @NotEmpty(message = "Must not be empty")
    @Size(min = 3, max = 40)
    private String username;
    @NotEmpty(message = "Must not be empty")
    @Size(min = 3, max = 80)
    private String email;
    @NotEmpty(message = "Must not be empty")
    @Size(min = 3, max = 80)
    private String password;
}
