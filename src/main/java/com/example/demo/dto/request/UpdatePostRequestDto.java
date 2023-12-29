package com.example.demo.dto.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequestDto {
    private String title;
    private String content;
    @Nullable
    private Set<String> tags;
}
