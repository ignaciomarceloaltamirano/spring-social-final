package com.example.demo.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentVoteRequestDto {
    @Pattern(regexp = "^(UPVOTE|DOWNVOTE)$")
    private String type;
}

