package com.example.demo.controller;

import com.example.demo.dto.request.VoteRequestDto;
import com.example.demo.dto.response.VoteResponseDto;
import com.example.demo.service.IVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    private final IVoteService voteService;

    @GetMapping("/user/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getCurrentVote(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(voteService.getCurrentVote(postId));
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<VoteResponseDto>> getPostVotes(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(voteService.getPostVotes(postId));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> votePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid VoteRequestDto voteRequestDto
    ) {
        return ResponseEntity.ok(voteService.votePost(postId, voteRequestDto));
    }
}
