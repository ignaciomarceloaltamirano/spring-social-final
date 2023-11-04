package com.example.demo.controller;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.auth.service.JwtService;
import com.example.demo.auth.service.UserDetailsServiceImpl;

import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.dto.response.PageDto;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Tag;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.impl.CommentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "MOD", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentServiceImpl commentService;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testGetPostComments() throws Exception {
        given(commentService.getPostComments(anyLong())).willReturn(Collections.singletonList(new CommentResponseDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/comments/post/{postId}", 1L)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testCreateComment_WithoutReplyToId_Success() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Test comment text")
                .build();

        given(commentService.createComment(anyLong(), any(CommentRequestDto.class)))
                .willReturn(CommentResponseDto.builder().text(commentRequestDto.getText()).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/comments/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto));

        mockMvc.perform(requestBuilder).andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentRequestDto.getText()));
    }

    @Test
    public void testCreateComment_WithReplyToId_Success() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Test comment text")
                .replyToId(1L)
                .build();

        given(commentService.createComment(anyLong(), any(CommentRequestDto.class)))
                .willReturn(CommentResponseDto.builder()
                        .text(commentRequestDto.getText())
                        .replyToId(1L)
                        .build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/comments/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentRequestDto.getText()))
                .andExpect(jsonPath("$.replyToId").value(1L));
    }

    @Test
    public void testCreateComment_WhenPostNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(commentService.createComment(anyLong(), any(CommentRequestDto.class)))
                .willThrow(new ResourceNotFoundException("Post not found"));

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Test comment text")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto)));
    }

    @Test
    public void testCreateComment_WhenCommentNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(commentService.createComment(anyLong(), any(CommentRequestDto.class)))
                .willThrow(new ResourceNotFoundException("Comment not found"));

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Test comment text")
                .replyToId(1L)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto)));
    }

    @Test
    public void testUpdateComment_Success() throws Exception {
        UpdateCommentRequestDto updateRequestDto = new UpdateCommentRequestDto();
        updateRequestDto.setText("Updated comment");

        given(commentService.updateComment(anyLong(), any(UpdateCommentRequestDto.class)))
                .willReturn(CommentResponseDto.builder().text(updateRequestDto.getText()).build());

        mockMvc.perform(MockMvcRequestBuilders.put("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(updateRequestDto.getText()));
    }

    @Test
    public void testUpdateComment_WhenCommentNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(commentService.updateComment(anyLong(), any(UpdateCommentRequestDto.class)))
                .willThrow(new ResourceNotFoundException("Comment not found"));

        UpdateCommentRequestDto updateRequestDto = new UpdateCommentRequestDto();
        updateRequestDto.setText("Updated comment");

        mockMvc.perform(MockMvcRequestBuilders.put("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteComment_Success() throws Exception {
        given(commentService.deleteComment(any())).willReturn(new MessageDto("Comment deleted"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/comments/{commentId}", 1L, 1)
                .accept(MediaType.APPLICATION_JSON);

      mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.message").value("Comment deleted"))
                .andReturn();
    }

    @Test
    public void testDeleteComment_WhenCommentNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(commentService.deleteComment(anyLong()))
                .willThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{commentId}", 1L))
                .andExpect(status().isNotFound());
    }
}
