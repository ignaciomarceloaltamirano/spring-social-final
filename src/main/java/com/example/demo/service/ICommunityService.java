package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;

public interface ICommunityService {
    PageDto<CommunityResponseDto> getAllCommunities(int page);
    CommunityResponseDto createCommunity(CommunityRequestDto communityRequestDto);

    MessageDto deleteCommunity(Long communityId);

    CommunityResponseDto updateCommunity(Long communityId, CommunityRequestDto communityRequestDto);
}
