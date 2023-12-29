package com.example.demo.controller;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.service.ISearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Search", description = "Endpoints related to search posts")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class SearchController {
    private final ISearchService searchService;

    @Operation(summary = "Get posts by title or author")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a page of a posts",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {PostResponseDto.class}))
                    }
            )
    })
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto<PostResponseDto>> getPostsByTitleOrAuthorContaining(
            @RequestParam("query") String query,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(searchService.getPostsByTitleOrAuthor(query, page));
    }
}
