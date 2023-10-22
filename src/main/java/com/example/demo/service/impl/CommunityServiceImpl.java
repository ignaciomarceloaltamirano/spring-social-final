package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.ICommunityService;
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
public class CommunityServiceImpl implements ICommunityService {
    private final CommunityRepository communityRepository;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    public PageDto<CommunityResponseDto> getAllCommunities(int page) {
        Sort s=Sort.by("id").ascending();

        Page<Community> pageRequest= communityRepository.findAll(PageRequest.of(page-1,2,s));
        int totalPages=pageRequest.getTotalPages();
        List<CommunityResponseDto> content =pageRequest.getContent().stream()
                .map(community -> modelMapper.map(community, CommunityResponseDto.class))
                .toList();
        return new PageDto<>(content,totalPages);
    }

    public CommunityResponseDto createCommunity(CommunityRequestDto communityRequestDto) {
        User user = utilService.getCurrentUser();
        Community community = Community.builder()
                .creator(user)
                .name(communityRequestDto.getName())
                .build();
        communityRepository.save(community);
        return modelMapper.map(community,CommunityResponseDto.class);
    }
    @Transactional
    public CommunityResponseDto updateCommunity(Long communityId, CommunityRequestDto communityRequestDto) {
        User user = utilService.getCurrentUser();
        Community community=communityRepository.findById(communityId)
                .orElseThrow(()->new ResourceNotFoundException("Community not found"));

        if(user!= community.getCreator()){
            throw  new UnauthorizedUserException("Not authorized");
        }

        if(!Objects.equals(community.getName(), communityRequestDto.getName()) &&
                !communityRequestDto.getName().isEmpty()
        ){
            community.setName(communityRequestDto.getName());
        }
        return modelMapper.map(community,CommunityResponseDto.class);
    }

    public MessageDto deleteCommunity(Long communityId) {
        User user = utilService.getCurrentUser();
        Community community=communityRepository.findById(communityId)
                .orElseThrow(()->new ResourceNotFoundException("Community not found"));
        if(user!= community.getCreator()){
            throw  new UnauthorizedUserException("Not authorized");
        }
        communityRepository.delete(community);
        return new MessageDto("Community deleted");
    }

}
