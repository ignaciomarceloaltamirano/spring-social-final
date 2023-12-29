package com.example.demo.controller;

import com.example.demo.dto.response.TagResponseDto;
import com.example.demo.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tag", description = "Endpoints related to tags")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class TagController {
    private final ITagService tagService;

    @Operation(summary = "Get all tags")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of tags",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = TagResponseDto.class))
                    }
            )
    })
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getTags() {
        return ResponseEntity.ok(tagService.getTags());
    }
}

