package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.ICommunityService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private ICommunityService communityService;
    @MockBean
    private CommunityRepository communityRepository;
    @MockBean
    private TokenRepository tokenRepository;
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
        given(communityService.getAllCommunities()).willReturn(List.of(new CommunityResponseDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/communities")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCommunity() throws Exception {
        given(communityService.getCommunity("test")).willReturn(new CommunityResponseDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/communities/{communityName}","test")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCommunity() throws Exception {
        CommunityRequestDto communityRequestDto = CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.createCommunity(communityRequestDto))
                .willReturn(CommunityResponseDto.builder().name(communityRequestDto.getName()).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/communities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(communityRequestDto.getName()));
    }

    @Test
    public void testCreateCommunity_WhenCommunityAlreadyExists_ThrowsResourceAlreadyExistsException() throws Exception {
        CommunityRequestDto communityRequestDto = CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.createCommunity(communityRequestDto))
                .willThrow(new ResourceNotFoundException("Community with name: " + communityRequestDto.getName() + " already exists."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/communities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCommunity_Success() throws Exception {
        CommunityRequestDto communityRequestDto = CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.updateCommunity(1L, communityRequestDto))
                .willReturn(CommunityResponseDto.builder().name(communityRequestDto.getName()).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/communities/{communityId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(communityRequestDto.getName()));
    }

    @Test
    public void testUpdateCommunity_WhenCommunityNotFound_ThrowsResourceNotFoundException() throws Exception {
        CommunityRequestDto communityRequestDto = CommunityRequestDto.builder()
                .name("test")
                .build();

        given(communityService.updateCommunity(1L, communityRequestDto))
                .willThrow(new ResourceNotFoundException("Community not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/communities/{communityId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(communityRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCommunity_Success() throws Exception {
        given(communityService.deleteCommunity(any()))
                .willReturn(new MessageDto("Community deleted"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/communities/{communityId}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Community deleted"));
    }

    @Test
    public void testDeleteCommunity_WhenCommunityNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(communityService.deleteCommunity(anyLong()))
                .willThrow(new ResourceNotFoundException("Community not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/communities/{commentId}", 1L))
                .andExpect(status().isNotFound());
    }
}
