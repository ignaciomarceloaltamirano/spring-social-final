package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String text;
    private Long authorId;
    private String authorName;
    private String authorImageUrl;
    private Long postId;
    private Long replyToId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

