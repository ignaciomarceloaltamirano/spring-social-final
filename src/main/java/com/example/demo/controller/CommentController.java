package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @GetMapping("/post/{postId}/page/{page}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageDto<CommentResponseDto>> getPostComments(
            @PathVariable Long postId,
            @PathVariable int page
    ){
        return ResponseEntity.ok(commentService.getPostComments(postId,page));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequestDto updateCommentRequestDto
    ){
        return ResponseEntity.ok(commentService.updateComment(commentId,updateCommentRequestDto));
    }

    @PostMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequestDto commentRequestDto
    ){
        return ResponseEntity.ok(commentService.createComment(postId,commentRequestDto));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> deleteComment(
            @PathVariable Long commentId
    ){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}

