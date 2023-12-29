package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;
import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.CommentVoteServiceImpl;
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


@WebMvcTest(CommentVoteController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class CommentVoteControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentVoteServiceImpl commentVoteService;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private CommentVoteRepository commentVoteRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetCurrentVote_Success() throws Exception {
        given(commentVoteService.getCurrentVote(anyLong())).willReturn(
                CommentVoteResponseDto.builder().type("UPVOTE").build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/commentvotes/user/{commentId}", anyLong());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("UPVOTE"));
    }

    @Test
    public void testGetCurrentVote_WhenCommentNotFound_ReturnsNull() throws Exception {
        given(commentVoteService.getCurrentVote(anyLong()))
                .willReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/commentvotes/user/{commentId}", anyLong());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testGetCurrentVote_WhenCommentVoteNotFound_ReturnsNull() throws Exception {
        given(commentVoteService.getCurrentVote(anyLong()))
                .willReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/commentvotes/user/{commentId}", anyLong());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testGetCommentVotes_Success() throws Exception {
        given(commentVoteService.getCommentVotes(anyLong()))
                .willReturn(List.of(new CommentVoteResponseDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/commentvotes/comment/{commentId}", anyLong());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCommentVotes_WhenCommentNotFound_ReturnsNull() throws Exception {
        given(commentVoteService.getCommentVotes(anyLong()))
                .willReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/commentvotes/comment/{commentId}", anyLong());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testCommentVote_WhenVoteDoesNotExist_ReturnsNewCommentVote() throws Exception {
        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder().type("UPVOTE").build();

        given(commentVoteService.commentVote(commentVoteRequestDto, 1L))
                .willReturn(CommentVoteResponseDto.builder().type(commentVoteRequestDto.getType()).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/commentvotes/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentVoteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("UPVOTE"));
    }

    @Test
    public void testCommentVote_WhenVoteTypeIsEqualToCurrent_DeletesCommentVote() throws Exception {
        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder().type("UPVOTE").build();

        given(commentVoteService.commentVote(commentVoteRequestDto, 1L))
                .willReturn(new MessageDto("Vote deleted"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/commentvotes/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentVoteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Vote deleted"));
    }

    @Test
    public void testCommentVote_WhenVoteTypeIsNotEqualToCurrent_ReturnsUpdatedCommentVote() throws Exception {
        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder().type("UPVOTE").build();

        given(commentVoteService.commentVote(commentVoteRequestDto, 1L))
                .willReturn(CommentVoteResponseDto.builder().type("UPVOTE").build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/commentvotes/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentVoteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("UPVOTE"));
    }


    @Test
    public void testCommentVote_WhenCommentNotFound_ThrowsResourceNotFoundException() throws Exception {
        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder().type("UPVOTE").build();

        given(commentVoteService.commentVote(commentVoteRequestDto, 1L))
                .willThrow(new ResourceNotFoundException("Comment not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/commentvotes/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentVoteRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
        ;
    }
}
