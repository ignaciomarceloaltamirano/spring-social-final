package com.example.demo.service.impl;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final TagRepository tagRepository;

    public List<Tag> getTags() {
        return tagRepository.findTagsWihPosts();
    }
}
