package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.CommentVote;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.enumeration.EVoteType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.CommentVoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentVoteServiceTests {
    @Mock
    private IUtilService utilService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentVoteRepository commentVoteRepository;
    @InjectMocks
    private CommentVoteServiceImpl commentVoteService;
    @Spy
    private ModelMapper modelMapper;

    private User user;
    private Comment comment;
    private CommentVote commentVote;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("user")
                .email("test@test.com")
                .password("test")
                .build();

        Post post = Post.builder().build();

        comment = Comment.builder()
                .id(1L)
                .text("Hello")
                .author(user)
                .post(post)
                .build();

        commentVote = CommentVote.builder()
                .id(1L)
                .user(user)
                .comment(comment)
                .type(EVoteType.DOWNVOTE)
                .build();
    }

    @Test
    void testGetCurrentVote_Success() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(commentVoteRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(commentVote));

        CommentVoteResponseDto result = commentVoteService.getCurrentVote(1L);

        assertNotNull(result);
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentVoteRepository, times(1)).findByUserIdAndCommentId(anyLong(), anyLong());
    }

    @Test
    void testGetCurrentVote_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                commentVoteService.getCurrentVote(1L));

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCurrentVote_WhenCommentVoteNotFound_ReturnsNull() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(commentVoteRepository.findByUserIdAndCommentId(anyLong(), anyLong())).willReturn(Optional.empty());

        CommentVoteResponseDto result = commentVoteService.getCurrentVote(1L);

        assertThat(result).isNull();
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCommentVotes_Success() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        List<CommentVoteResponseDto> result = commentVoteService.getCommentVotes(anyLong());

        assertNotNull(result);
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCommentVotes_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                commentVoteService.getCommentVotes(1L));

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testCommentVote_WhenCommentVoteDoesNotExist_ReturnsNewCommentVote() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(commentVoteRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder()
                .type("UPVOTE")
                .build();

        Object result = commentVoteService.commentVote(commentVoteRequestDto, 1L);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CommentVoteResponseDto.class);
        verify(commentVoteRepository,times(1)).save(any(CommentVote.class));
    }

    @Test
    void testCommentVote_WhenCommentVoteExistsAndTypeIsNotEqualToCommentVoteRequestDto_ReturnsUpdatedCommentVote() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(commentVoteRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(commentVote));

        CommentVote commentVoteToUpdate = CommentVote.builder()
                .id(1L)
                .user(user)
                .comment(comment)
                .type(EVoteType.DOWNVOTE)
                .build();

        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder()
                .type("UPVOTE")
                .build();

        commentVoteToUpdate.setType(EVoteType.valueOf(commentVoteRequestDto.getType()));

        Object result = commentVoteService.commentVote(commentVoteRequestDto, 1L);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CommentVoteResponseDto.class);
    }

    @Test
    void testCommentVote_WhenCommentVoteExistsAndTypeIsEqualToCommentVoteRequestDto_ReturnsMessageDto() {
        given(utilService.getCurrentUser()).willReturn(user);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        given(commentVoteRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(commentVote));

        CommentVote commentVoteToUpdate = CommentVote.builder()
                .id(1L)
                .user(user)
                .comment(comment)
                .type(EVoteType.DOWNVOTE)
                .build();

        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder()
                .type("DOWNVOTE")
                .build();

        Object result = commentVoteService.commentVote(commentVoteRequestDto, 1L);
        assertThat(result).isInstanceOf(MessageDto.class);
    }

    @Test
    void testCommentVote_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        CommentVoteRequestDto commentVoteRequestDto = CommentVoteRequestDto.builder()
                .type("DOWNVOTE")
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                commentVoteService.commentVote(commentVoteRequestDto, 1L));

        verify(commentRepository, times(1)).findById(anyLong());
    }
}
