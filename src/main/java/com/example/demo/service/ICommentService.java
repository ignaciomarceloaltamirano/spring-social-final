package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;

import java.util.List;

public interface ICommentService {
    CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto);
    MessageDto deleteComment(Long commentId);
    List<CommentResponseDto> getPostComments(Long postId);
    CommentResponseDto updateComment(Long commentId, UpdateCommentRequestDto updateCommentRequestDto);
}
