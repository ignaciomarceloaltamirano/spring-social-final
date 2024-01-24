package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.ICommentService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

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

    public List<CommentResponseDto> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        List<Comment> comments = commentRepository.findAllByPost(post);

        return comments.stream().map(comment -> modelMapper.map(comment, CommentResponseDto.class)).toList();
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

        if (user != comment.getAuthor()) {
            throw new UnauthorizedUserException("Not authorized.");
        }

        if (!Objects.equals(comment.getText(), updateCommentRequestDto.getText()) &&
                !updateCommentRequestDto.getText().isEmpty()
        ) {
            comment.setText(updateCommentRequestDto.getText());
        }
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    public MessageDto deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = utilService.getCurrentUser();
        if (user != comment.getAuthor()) {
            throw new UnauthorizedUserException("Not authorized.");
        }

        commentRepository.deleteById(commentId);
        return new MessageDto("Comment deleted");
    }
}
