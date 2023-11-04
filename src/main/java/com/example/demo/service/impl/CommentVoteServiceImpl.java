package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommentVoteRequestDto;
import com.example.demo.dto.response.CommentVoteResponseDto;
import com.example.demo.dto.response.VoteResponseDto;
import com.example.demo.entity.*;
import com.example.demo.enumeration.EVoteType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
import com.example.demo.service.ICommentVoteService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentVoteServiceImpl implements ICommentVoteService {
    private final IUtilService utilService;
    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final ModelMapper modelMapper;

    public CommentVoteResponseDto getCurrentVote(Long commentId) {
        User user = utilService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return null;
        } else {
            CommentVote commentVote = commentVoteRepository.findByUserIdAndCommentId(user.getId(), comment.getId())
                    .orElse(null);
            if (commentVote == null) {
                return null;
            }
            return modelMapper.map(commentVote, CommentVoteResponseDto.class);
        }
    }

    public List<CommentVoteResponseDto> getCommentVotes(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if(comment==null){
            return  null;
        }else{

        return commentVoteRepository.findAllByComment(comment).stream().map(
                commentVote -> modelMapper.map(commentVote, CommentVoteResponseDto.class)).toList();
        }
    }

    @Transactional
    public Object commentVote(CommentVoteRequestDto commentVoteRequestDto, Long commentId) {
        User user = utilService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        CommentVote existingVote = commentVoteRepository.findByUserIdAndCommentId(user.getId(), comment.getId())
                .orElse(null);
        EVoteType voteType = EVoteType.valueOf(commentVoteRequestDto.getType());

        if (existingVote != null) {
            if (voteType != existingVote.getType()) {
                existingVote.setType(voteType);
                return modelMapper.map(existingVote, CommentVoteResponseDto.class);
            } else {
                commentVoteRepository.deleteByUserIdAndCommentId(user.getId(), commentId);
                return new MessageDto("Vote deleted");
            }
        }
        CommentVote newCommentVote = CommentVote.builder()
                .user(user)
                .comment(comment)
                .type(voteType)
                .build();
        commentVoteRepository.save(newCommentVote);
        return modelMapper.map(newCommentVote, CommentVoteResponseDto.class);
    }
}

