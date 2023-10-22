package com.example.demo.service.impl;

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
import com.example.demo.service.ICommentService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final IUtilService utilService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public PageDto<CommentResponseDto> getPostComments(Long postId, int page) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        Sort s = Sort.by("id").ascending();

        Page<Comment> pageRequest = commentRepository.findAllByPost(post, PageRequest.of(page - 1, 2, s));

        int totalPages = pageRequest.getTotalPages();
        List<CommentResponseDto> content = pageRequest
                .getContent().stream().map(comment -> modelMapper.map(comment, CommentResponseDto.class)).toList();

        return new PageDto<>(content, totalPages);
    }

    public CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto) {
        User user = utilService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = Comment.builder()
                .author(user)
                .post(post)
                .text(commentRequestDto.getText())
                .build();

        if (commentRequestDto.getReplyToId() != null) {
            Comment replyTo = commentRepository.findById(commentRequestDto.getReplyToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            comment.setReplyTo(replyTo);
        }
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, UpdateCommentRequestDto updateCommentRequestDto) {
        User user = utilService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!Objects.equals(comment.getText(), updateCommentRequestDto.getText()) &&
                !updateCommentRequestDto.getText().isEmpty() &&
                comment.getAuthor() == user
        ) {
            comment.setText(updateCommentRequestDto.getText());
        }
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    public MessageDto deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.deleteById(commentId);
        return new MessageDto("Comment deleted");
    }
}
