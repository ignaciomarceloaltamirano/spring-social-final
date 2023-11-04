package com.example.demo.service.impl;

import com.example.demo.auth.dto.response.MessageDto;
import com.example.demo.dto.request.CommunityRequestDto;
import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.dto.response.PageDto;
import com.example.demo.entity.Community;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceAlreadyExists;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedUserException;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.ICommunityService;
import com.example.demo.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements ICommunityService {
    private final CommunityRepository communityRepository;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    public List<CommunityResponseDto> getAllCommunities() {
        List<Community> communities= communityRepository.findAll();
              return  communities.stream().map(community -> modelMapper.map(community,CommunityResponseDto.class)).toList();
    }

    public CommunityResponseDto getCommunity(String name) {
        Community community= communityRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException("Community not found."));

        return modelMapper.map(community,CommunityResponseDto.class);
    }

    public CommunityResponseDto createCommunity(CommunityRequestDto communityRequestDto) {
        boolean existsCommunity=communityRepository.existsByName(communityRequestDto.getName());
        if(existsCommunity){
            throw new ResourceAlreadyExists("Community with name: " + communityRequestDto.getName() + " already exists.");
        }
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
                .orElseThrow(()->new ResourceNotFoundException("Community not found."));

        if(user!= community.getCreator()){
            throw  new UnauthorizedUserException("Not authorized.");
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
                .orElseThrow(()->new ResourceNotFoundException("Community not found."));
        if(user!= community.getCreator()){
            throw  new UnauthorizedUserException("Not authorized.");
        }
        communityRepository.delete(community);
        return new MessageDto("Community deleted.");
    }

}
