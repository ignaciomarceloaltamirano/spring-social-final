package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.impl.CommunityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommunityServiceTests {
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private IUtilService utilService;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private CommunityServiceImpl communityService;
    private User user;
    private User unauthorizedUser;
    private Community community;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();

        unauthorizedUser = User.builder()
                .username("unauthorized")
                .email("test@unauthorized.com")
                .password("test")
                .build();

        community = Community.builder()
                .id(1L)
                .name("testcommunity")
                .creator(user)
                .build();
    }

    @Test
    void testGetPostComments() {
        Page<Community> pageRequest = new PageImpl<>(Collections.singletonList(community));
        given(communityRepository.findAll(any(PageRequest.class))).willReturn(pageRequest);

        PageDto<CommunityResponseDto> result = communityService.getAllCommunities(1);

        assertNotNull(result);
        verify(communityRepository, times(1)).findAll(any(PageRequest.class));
        assertThat(result).isInstanceOf(PageDto.class);
    }

    @Test
    void testCreateCommunity() {
        CommunityRequestDto communityRequestDto = CommunityRequestDto.builder()
                .name("newcommunity")
                .build();

        CommunityResponseDto result = communityService.createCommunity(communityRequestDto);
        assertThat(result).isNotNull();
        assertEquals("newcommunity", result.getName());
        verify(communityRepository, times(1)).save(any(Community.class));
    }

    @Test
    void testUpdateCommunity_Success() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(community));

        CommunityRequestDto communityRequestDto = new CommunityRequestDto();
        communityRequestDto.setName("updated");

        CommunityResponseDto result = communityService.updateCommunity(1L, communityRequestDto);

        assertNotNull(result);
        assertEquals(communityRequestDto.getName(), result.getName());
    }

    @Test
    void testUpdateCommunity_WhenCommunityNotFound_ThrowsResourceNotFoundException() {
        given(communityRepository.findById(anyLong())).willReturn(Optional.empty());

        CommunityRequestDto communityRequestDto = new CommunityRequestDto();
        communityRequestDto.setName("hello");

        assertThrows(ResourceNotFoundException.class, () ->
                communityService.updateCommunity(1L, communityRequestDto));

        verify(communityRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateCommunity_WhenUserIsNotEqualToCreator_ThrowsUnauthorizedUserException() {
        given(utilService.getCurrentUser()).willReturn(unauthorizedUser);
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(community));

        CommunityRequestDto communityRequestDto = new CommunityRequestDto();
        communityRequestDto.setName("hello");

        assertThrows(UnauthorizedUserException.class, () ->
                communityService.updateCommunity(1L, communityRequestDto));

        verify(communityRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeleteCommunity_Success() {
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(community));
        given(utilService.getCurrentUser()).willReturn(user);

        MessageDto result = communityService.deleteCommunity(1L);

        assertNotNull(result);
        assertEquals("Community deleted", result.getMessage());
        verify(communityRepository, times(1)).delete(community);
    }

    @Test
    void testDeleteCommunity_WhenUserIsNotEqualToCreator_ThrowsUnauthorizedUserException() {
        given(utilService.getCurrentUser()).willReturn(unauthorizedUser);
        given(communityRepository.findById(anyLong())).willReturn(Optional.of(community));

        assertThrows(UnauthorizedUserException.class, () ->
                communityService.deleteCommunity(1L));

        verify(communityRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeleteCommunity_WhenCommunityNotFound_ThrowsResourceNotFoundException() {
        given(communityRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                communityService.deleteCommunity(1L));

        verify(communityRepository, never()).delete(any());
    }
}
