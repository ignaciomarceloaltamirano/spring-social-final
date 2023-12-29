package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "Endpoints related to comments")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class CommentController {
    private final ICommentService commentService;

    @Operation(summary = "Get all comments from a post")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of comment",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CommentResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getPostComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getPostComments(postId));
    }

    @Operation(summary = "Create a comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created a comment",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = CommentResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequestDto commentRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, commentRequestDto));
    }

    @Operation(summary = "Update a comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated a comment",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = CommentResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequestDto updateCommentRequestDto
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, updateCommentRequestDto));
    }

    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted a comment",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageDto> deleteComment(
            @PathVariable Long commentId
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(commentService.deleteComment(commentId));
    }
}

