package com.example.demo.service;

import com.example.demo.dto.response.TagResponseDto;
import com.example.demo.entity.Tag;

import java.util.List;

public interface ITagService {
    List<TagResponseDto> getTags();
}