package com.example.demo.controller;

import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.CommunityServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class CommunityControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommunityServiceImpl communityService;
    @MockBean
    private CommunityRepository communityRepository;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetCommunities() throws Exception {
        given(communityService.getAllCommunities(anyInt()))
                .willReturn(new PageDto<>(Collections.singletonList(new CommunityResponseDto()), anyInt()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/communities/page/{page}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty())
                .andReturn();
    }

    @Test
    public void testCreateCommunity() throws Exception {
        CommunityRequestDto communityRequestDto= CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.createCommunity(communityRequestDto))
                .willReturn(CommunityResponseDto.builder().name(communityRequestDto.getName()).build());

        RequestBuilder requestBuilder=MockMvcRequestBuilders.post("/communities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(communityRequestDto.getName()))
                .andReturn();
    }

    @Test
    public void testUpdateCommunity_Success() throws Exception {
        CommunityRequestDto communityRequestDto= CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.updateCommunity(1L,communityRequestDto))
                .willReturn(CommunityResponseDto.builder().name(communityRequestDto.getName()).build());

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .put("/communities/{communityId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(communityRequestDto.getName()))
                .andReturn();
        }

    @Test
    public void testUpdateCommunity_WhenCommunityNotFound_ThrowsResourceNotFoundException() throws Exception {
        CommunityRequestDto communityRequestDto= CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.updateCommunity(1L,communityRequestDto))
                .willThrow(new ResourceNotFoundException("Community not found"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .put("/communities/{communityId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testUpdateCommunity_WhenUserIsNotEqualToCreator_ThrowsUnauthorizedUserException() throws Exception {
        CommunityRequestDto communityRequestDto= CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.updateCommunity(1L,communityRequestDto))
                .willThrow(new UnauthorizedUserException("Not authorized"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .put("/communities/{communityId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
