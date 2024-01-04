package com.example.demo.dto.request;

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
public class UpdatePasswordRequestDto {
    @NotEmpty(message = "Current Password must not be empty.")
    @Size(min = 3, max = 80, message = "Current password must be between 3 and 80 characters long.")
    private String currentPassword;
    @NotEmpty(message = "New Password must not be empty.")
    @Size(min = 3, max = 80, message = "New password must be between 3 and 80 characters long.")
    private String newPassword;
    @NotEmpty(message = "Confirmation Password must not be empty.")
    @Size(min = 3, max = 80, message = "Confirmation password must be between 3 and 80 characters long.")
    private String confirmationPassword;
}
