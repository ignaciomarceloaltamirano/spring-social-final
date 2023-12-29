package com.example.demo.controller;

import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.service.ICommentVoteService;
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

@Tag(name = "Comment Vote", description = "Endpoints related to comment votes")
@RestController
@RequestMapping("/commentvotes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class CommentVoteController {
    private final ICommentVoteService commentVoteService;

    @Operation(summary = "Get user's current comment vote")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found current vote",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))
                    }
            )
    })
    @GetMapping("/user/{commentId}")
    public ResponseEntity<Object> getCurrentVote(
            @PathVariable("commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentVoteService.getCurrentVote(commentId));
    }

    @Operation(summary = "Get all comment votes from a comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of comment votes",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CommentVoteResponseDto.class))
                    }
            )
    })
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<CommentVoteResponseDto>> getCommentVotes(
            @PathVariable("commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentVoteService.getCommentVotes(commentId));
    }

    @Operation(summary = "Vote a comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Voted for a comment",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Object> voteComment(
            @RequestBody @Valid CommentVoteRequestDto commentVoteRequestDto,
            @PathVariable("commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentVoteService.commentVote(commentVoteRequestDto, commentId));
    }
}

