package com.example.demo.service;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.User;
import com.example.demo.repository.CommunityRepository;
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
    private CommunityRepository communityRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void testGetAllCommunities() {
        Page<Community> page = new PageImpl<>(Collections.singletonList(new Community()));
        given(communityRepository.findAllByNameContaining(anyString(),any(PageRequest.class))).willReturn(page);

        PageDto<CommunityResponseDto> result = searchService.getCommunitiesByName("test", 1);
        assertThat(result).isNotNull();
        verify(communityRepository, times(1)).findAllByNameContaining(anyString(),any(PageRequest.class));
        assertThat(result).isInstanceOf(PageDto.class);
    }
}
