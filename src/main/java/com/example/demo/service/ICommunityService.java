package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;

import java.util.List;
import java.util.Optional;

public interface ICommunityService {
    List<CommunityResponseDto> getAllCommunities();
    CommunityResponseDto createCommunity(CommunityRequestDto communityRequestDto);
    MessageDto deleteCommunity(Long communityId);
    CommunityResponseDto updateCommunity(Long communityId, CommunityRequestDto communityRequestDto);
    CommunityResponseDto getCommunity(String name);
}
