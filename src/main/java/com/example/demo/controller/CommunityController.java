package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.service.ICommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final ICommunityService communityService;

    @GetMapping("/page/{page}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageDto<CommunityResponseDto>> getCommunities(
            @PathVariable("page")int page
    ){
        return ResponseEntity.ok(communityService.getAllCommunities(page));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ){
        return ResponseEntity.ok(communityService.createCommunity(communityRequestDto));
    }

    @PutMapping("/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable("communityId")Long communityId,
            @RequestBody @Valid CommunityRequestDto communityRequestDto
    ){
        return ResponseEntity.ok(communityService.updateCommunity(communityId, communityRequestDto));
    }

    @DeleteMapping("/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MOD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> deleteCommunity(
            @PathVariable("communityId")Long communityId
    ){
        return ResponseEntity.ok(communityService.deleteCommunity(communityId));
    }
}

