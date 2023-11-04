package com.example.demo.controller;

import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.PostResponseDto;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.SearchServiceImpl;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class SearchControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SearchServiceImpl searchService;
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
    public void testPostsByTitleOrAuthorContaining() throws Exception {
        CommunityResponseDto communityResponseDto = CommunityResponseDto.builder()
                .name("test")
                .build();

        given(searchService.getPostsByTitleOrAuthor("test", 1))
                .willReturn(new PageDto<>(List.of(new PostResponseDto()), anyInt(),anyInt()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/search/page/{page}", 1)
                .param("query", "test")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.currentPage").exists())
                .andExpect(jsonPath("$.totalPages").isNotEmpty())
                .andReturn();
    }
}
