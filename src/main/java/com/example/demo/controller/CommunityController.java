package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.service.ICommunityService;
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

@Tag(name = "Community", description = "Endpoints related to communities")
@RestController
@RequestMapping("/communities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class CommunityController {
    private final ICommunityService communityService;

    @Operation(summary = "Get all communities")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of communities",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CommunityResponseDto.class))
                    }
            )
    })
    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @Operation(summary = "Get a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a community",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Community not found"
            )
    })
    @GetMapping("/{communityName}")
    public ResponseEntity<CommunityResponseDto> getCommunity(
            @PathVariable("communityName") String name
    ) {
        return ResponseEntity.ok(communityService.getCommunity(name));
    }

    @Operation(summary = "Create a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created a community",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = CommunityResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Community name already exists"
            )
    })
    @PostMapping
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.createCommunity(communityRequestDto));
    }

    @Operation(summary = "Update a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated a community",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = CommunityResponseDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable("communityId") Long communityId,
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ) {
        return ResponseEntity.ok(communityService.updateCommunity(communityId, communityRequestDto));
    }

    @Operation(summary = "Delete a community")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted a community",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Community not found"
            )
    })
    @DeleteMapping("/{communityId}")
    public ResponseEntity<MessageDto> deleteCommunity(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(communityService.deleteCommunity(communityId));
    }
}

