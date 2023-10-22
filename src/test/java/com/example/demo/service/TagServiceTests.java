package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void getTagsWithPosts() {
        Tag tag1 = Tag.builder()
                .name("tag1")
                .build();

        Tag tag2 = Tag.builder()
                .name("tag2")
                .build();

        given(tagRepository.findTagsWihPosts()).willReturn(List.of(tag1,tag2));

        List<Tag> result = tagService.getTags();

        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(0);
    }
}
