package com.example.demo.service;

import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;

import java.util.List;

public interface ICommentVoteService {
    CommentVoteResponseDto getCurrentVote(Long commentId);
    List<CommentVoteResponseDto> getCommentVotes(Long comment);
    Object commentVote(CommentVoteRequestDto commentVoteRequestDto, Long commentId);
}

