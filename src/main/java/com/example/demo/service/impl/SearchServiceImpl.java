package com.example.demo.service.impl;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Community;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements ISearchService {
    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;

    public PageDto<CommunityResponseDto> getCommunitiesByName(String query, int page) {
        Sort s = Sort.by("id").ascending();
        Page<Community> pageRequest = communityRepository.findAllByNameContaining(query, PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<CommunityResponseDto> content = pageRequest
                .getContent().stream().map(post -> modelMapper.map(post, CommunityResponseDto.class)).toList();
        return new PageDto<>(content, totalPages);
    }
}
