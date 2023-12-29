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
public class CommunityRequestDto {
    @NotEmpty(message = "Name must not be empty.")
    @Size(min = 3, max = 120, message = "Size must be between 3 and 120 characters long.")
    private String name;
}

