package com.example.demo.service;

import com.example.demo.dto.request.VoteRequestDto;
import com.example.demo.dto.response.VoteResponseDto;

import java.util.List;

public interface IVoteService {
    Object votePost(Long postId, VoteRequestDto voteRequestDto);
    String getCurrentVote(Long postId);
    List<VoteResponseDto> getPostVotes(Long postId);
}
