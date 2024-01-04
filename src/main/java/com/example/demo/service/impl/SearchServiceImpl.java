package com.example.demo.service.impl;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PageDto<PostResponseDto> getPostsByTitleOrAuthor(String query, int page) {
        Sort s = Sort.by("id").descending();

        Page<Post> pageRequest = postRepository.findPostsByTitleOrAuthorContaining(query, PageRequest.of(page - 1, 6, s));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;

        List<PostResponseDto> content = pageRequest
                .getContent().stream().map(
                        post -> modelMapper.map(post, PostResponseDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage);
    }
}
