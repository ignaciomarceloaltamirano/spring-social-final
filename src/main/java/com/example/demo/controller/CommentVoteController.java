package com.example.demo.controller;

import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.service.ICommentVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commentvotes")
@RequiredArgsConstructor
public class CommentVoteController {
    private final ICommentVoteService commentVoteService;

    @GetMapping("/user/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentVoteResponseDto> getCurrentVote(
            @PathVariable("commentId")Long commentId
    ){
        return ResponseEntity.ok(commentVoteService.getCurrentVote(commentId));
    }

    @GetMapping("/comment/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CommentVoteResponseDto>> getCommentVotes(
            @PathVariable("commentId")Long commentId
    ){
        return ResponseEntity.ok(commentVoteService.getCommentVotes(commentId));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> voteComment(
            @RequestBody @Valid CommentVoteRequestDto commentVoteRequestDto,
            @PathVariable("commentId")Long commentId
    ){
        return ResponseEntity.ok(commentVoteService.commentVote(commentVoteRequestDto,commentId));
    }
}

