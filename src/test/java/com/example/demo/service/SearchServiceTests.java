package com.example.demo.service;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTests {
    @Mock
    private PostRepository postRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void testGetPostsByTitleOrAuthor() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));
        given(postRepository.findPostsByTitleOrAuthorContaining(anyString(),any(PageRequest.class))).willReturn(page);

        PageDto<PostResponseDto> result = searchService.getPostsByTitleOrAuthor("test", 1);

        assertThat(result).isNotNull();
        verify(postRepository, times(1)).findPostsByTitleOrAuthorContaining(anyString(),any(PageRequest.class));
        assertThat(result).isInstanceOf(PageDto.class);
    }
}
