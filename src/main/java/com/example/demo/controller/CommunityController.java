package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.service.ICommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
public class CommunityController {
    private final ICommunityService communityService;

    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{communityName}")
    public ResponseEntity<CommunityResponseDto> getCommunity(
            @PathVariable("communityName") String name
    ) {
        return ResponseEntity.ok(communityService.getCommunity(name));
    }

    @PostMapping
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ) {
        return ResponseEntity.ok(communityService.createCommunity(communityRequestDto));
    }

    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable("communityId") Long communityId,
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ) {
        return ResponseEntity.ok(communityService.updateCommunity(communityId, communityRequestDto));
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<MessageDto> deleteCommunity(
            @PathVariable("communityId") Long communityId
    ) {
        return ResponseEntity.ok(communityService.deleteCommunity(communityId));
    }
}

