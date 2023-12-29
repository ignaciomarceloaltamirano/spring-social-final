package com.example.demo.service.impl;

import com.example.demo.dto.response.TagResponseDto;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.ITagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public List<TagResponseDto> getTags() {
        return tagRepository.findTagsWihPosts().stream()
                .map(tag -> modelMapper.map(tag,TagResponseDto.class)).toList();
    }
}
