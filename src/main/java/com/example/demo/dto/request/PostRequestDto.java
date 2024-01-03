package com.example.demo.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    @NotEmpty(message = "Title must not be empty.")
    @Size(min = 3, max = 80, message = "Title must be between 3 and 120 characters long.")
    private String title;
    @NotEmpty(message = "Content must not be empty.")
    @Size(min = 3, max = 256, message = "Content must be between 3 and 256 characters long.")
    private String content;
    @Nullable
    private Set<String> tags;
}

