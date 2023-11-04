package com.example.demo.service;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;

import java.util.List;

public interface ISearchService {
    PageDto<PostResponseDto> getPostsByTitleOrAuthor(String query, int page);
}
