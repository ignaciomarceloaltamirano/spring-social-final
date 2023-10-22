package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.impl.CommentServiceImpl;
import com.example.demo.service.impl.UtilServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UtilServiceImpl utilService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setup() {
        author = User.builder()
                .id(1L)
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();

        post= Post.builder().id(1L).build();

        comment = Comment.builder()
                .id(1L)
                .text("Hello")
                .author(author)
                .post(post)
                .build()
        ;
    }

    @Test
    void testGetPostComments_Success() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        Page<Comment> pageRequest = new PageImpl<>(Collections.singletonList(comment));
        given(commentRepository.findAllByPost(eq(post), any(PageRequest.class))).willReturn(pageRequest);

        PageDto<CommentResponseDto> result = commentService.getPostComments(anyLong(), 1);

        assertNotNull(result);
        assertThat(result).isInstanceOf(PageDto.class);
        verify(commentRepository, times(1)).findAllByPost(any(Post.class), any(PageRequest.class));
    }

    @Test
    void testGetPostComments_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                commentService.getPostComments(anyLong(), 1));

        verify(commentRepository, never()).findAllByPost(any(), any());
    }

    @Test
    void testCreateComment_Success() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Hello");

        CommentResponseDto savedComment = commentService.createComment(1L, commentRequestDto);
        assertNotNull(savedComment);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WithReplyTo_Success() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Hello");
        commentRequestDto.setReplyToId(1L);

        CommentResponseDto savedComment = commentService.createComment(1L, commentRequestDto);

        assertNotNull(savedComment);
        assertEquals(1L,savedComment.getReplyToId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenPostNotFound_ThrowsResourceNotFoundException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Hello").build();

        assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, commentRequestDto));

        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenReplyToCommentNotFound_ThrowsResourceNotFoundException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Hello");
        commentRequestDto.setReplyToId(2L);

        assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, commentRequestDto));

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testUpdateComment_Success() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(utilService.getCurrentUser()).willReturn(author);

        UpdateCommentRequestDto updateCommentRequestDto = new UpdateCommentRequestDto();
        updateCommentRequestDto.setText("Updated text");

        CommentResponseDto result = commentService.updateComment(1L, updateCommentRequestDto);

        assertEquals(comment.getText(), result.getText());
        assertNotNull(result);
        assertThat(result.getText()).isEqualTo(updateCommentRequestDto.getText());
    }

    @Test
    void testUpdateComment_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        UpdateCommentRequestDto updateCommentDto = new UpdateCommentRequestDto();
        updateCommentDto.setText("Hello");

        assertThrows(ResourceNotFoundException.class, () ->
                commentService.updateComment(1L, updateCommentDto));

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testDeleteComment_Success() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        MessageDto result = commentService.deleteComment(anyLong());

        assertNotNull(result);
        assertEquals("Comment deleted", result.getMessage());
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteComment_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(anyLong()));

        verify(commentRepository, never()).deleteById(any());
    }
}
