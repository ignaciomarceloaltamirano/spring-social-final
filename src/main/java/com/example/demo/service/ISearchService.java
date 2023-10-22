package com.example.demo.service;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;

public interface ISearchService {
    PageDto<CommunityResponseDto> getCommunitiesByName(String query, int page);
}
