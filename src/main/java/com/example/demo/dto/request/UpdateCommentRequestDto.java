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
public class UpdateCommentRequestDto {
    @NotEmpty
    @Size(max = 255,message = "Text must be at most 255 characters long.")
    private String text;
}

