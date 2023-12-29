package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.request.VoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.dto.response.VoteResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.VoteServiceImpl;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class VoteControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private VoteRepository voteRepository;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private VoteServiceImpl voteService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetCurrentVote_WhenVoteExists_ReturnsVote() throws Exception {
        given(voteService.getCurrentVote(anyLong())).willReturn(new VoteResponseDto());

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/votes/user/{postId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentVote_WhenVoteDoesNotExist_ReturnsNull() throws Exception {
        given(voteService.getCurrentVote(anyLong())).willReturn(null);

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/votes/user/{postId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentVote_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(voteService.getCurrentVote(anyLong())).willThrow(new ResourceNotFoundException("Post not found"));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/votes/user/{postId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPostVotes() throws Exception {
        given(voteService.getPostVotes(anyLong())).willReturn(List.of(new VoteResponseDto()));

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .get("/votes/post/{postId}",anyLong())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testVotePost_WhenVoteDoesNotExist_ReturnsNewVote() throws Exception {
        VoteRequestDto voteRequestDto=VoteRequestDto.builder().type("UPVOTE").build();
        given(voteService.votePost(1L,voteRequestDto))
                .willReturn(VoteResponseDto.builder().type(voteRequestDto.getType()).build());

        RequestBuilder requestBuilder= MockMvcRequestBuilders
                .put("/votes/{postId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(voteRequestDto.getType()));
    }

    @Test
    public void testVotePost_WhenVoteTypeIsEqualToCurrent_DeletesVote() throws Exception {
        VoteRequestDto voteRequestDto=VoteRequestDto.builder().type("UPVOTE").build();

        given(voteService.votePost(1L, voteRequestDto))
                .willReturn(new MessageDto("Vote deleted"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/votes/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Vote deleted"));
    }

    @Test
    public void testVotePost_WhenVoteTypeIsNotEqualToCurrent_ReturnsUpdatedVote() throws Exception {
        VoteRequestDto voteRequestDto=VoteRequestDto.builder().type("UPVOTE").build();

        given(voteService.votePost(1L,voteRequestDto))
                .willReturn(CommentVoteResponseDto.builder().type("UPVOTE").build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/votes/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("UPVOTE"));
    }


    @Test
    public void testVotePost_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        VoteRequestDto voteRequestDto=VoteRequestDto.builder().type("UPVOTE").build();

        given(voteService.votePost(1L,voteRequestDto))
                .willThrow(new ResourceNotFoundException("Post not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/votes/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

}
