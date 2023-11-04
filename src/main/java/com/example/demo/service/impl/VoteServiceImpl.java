package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.VoteRequestDto;
import com.example.demo.dto.response.VoteResponseDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.entity.Vote;
import com.example.demo.enumeration.EVoteType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.service.IUtilService;
import com.example.demo.service.IVoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements IVoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    public VoteResponseDto getCurrentVote(Long postId) {
        User user = utilService.getCurrentUser();
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Vote vote = voteRepository.findByUserIdAndPostId(user.getId(), postOptional.get().getId());
            return vote != null ? modelMapper.map(vote,VoteResponseDto.class) : null;
        } else {
            throw new ResourceNotFoundException("Post not found");
        }
    }

    public List<VoteResponseDto> getPostVotes(Long postId) {
        return voteRepository.findAllByPostId(postId)
                .stream().map(vote -> modelMapper.map(vote, VoteResponseDto.class)).toList();
    }

    @Transactional
    public Object votePost(Long postId, VoteRequestDto voteRequestDto) {
        User user = utilService.getCurrentUser();
        Vote existingVote = voteRepository.findByUserIdAndPostId(user.getId(), postId);

        EVoteType voteType = EVoteType.valueOf(voteRequestDto.getType());

        if (existingVote != null) {
            if (existingVote.getType().equals(voteType)) {
                voteRepository.deleteByUserIdAndPostId(user.getId(), postId);
                return new MessageDto("Vote deleted");
            } else {
                Vote updatedVote = voteRepository
                        .findByUserIdAndPostId(user.getId(), postId);
                updatedVote.setType(voteType);
                return modelMapper.map(updatedVote, VoteResponseDto.class);
            }
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            Vote newVote = Vote.builder()
                    .user(user)
                    .post(post)
                    .type(voteType)
                    .build();
            voteRepository.save(newVote);
            return modelMapper.map(newVote, VoteResponseDto.class);
        }
    }
}
