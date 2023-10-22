package com.example.demo.service;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentRequestDto;
import com.example.demo.dto.request.UpdateCommentRequestDto;
import com.example.demo.dto.response.CommentResponseDto;
import com.example.demo.dto.response.PageDto;

public interface ICommentService {
    CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto);
    MessageDto deleteComment(Long commentId);
    PageDto<CommentResponseDto> getPostComments(Long postId, int page);

    CommentResponseDto updateComment(Long commentId, UpdateCommentRequestDto updateCommentRequestDto);
}
