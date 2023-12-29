package com.example.demo.controller;

import com.example.demo.dto.request.VoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.dto.response.VoteResponseDto;
import com.example.demo.service.IVoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Vote", description = "Endpoints related to post votes")
@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class VoteController {
    private final IVoteService voteService;

    @Operation(summary = "Get user's current post vote")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found current post vote",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteResponseDto.class))
                    }
            )
    })
    @GetMapping("/user/{postId}")
    public ResponseEntity<VoteResponseDto> getCurrentVote(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(voteService.getCurrentVote(postId));
    }

    @Operation(summary = "Get all votes from a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of post votes",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = VoteResponseDto.class))
                    }
            )
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<VoteResponseDto>> getPostVotes(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(voteService.getPostVotes(postId));
    }

    @Operation(summary = "Vote a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Voted for a post",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PutMapping("/{postId}")
    public ResponseEntity<Object> votePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid VoteRequestDto voteRequestDto
    ) {
        return ResponseEntity.ok(voteService.votePost(postId, voteRequestDto));
    }
}
