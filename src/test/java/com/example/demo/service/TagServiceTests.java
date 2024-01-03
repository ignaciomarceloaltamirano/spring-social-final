package com.example.demo.service;

import com.example.demo.dto.response.TagResponseDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.Tag;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.nio.file.LinkOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private TagServiceImpl tagService;

    @Spy
    private ModelMapper modelMapper;

    @Test
    void getTagsWithPosts() {
        Post post1 = Post.builder()
                .id(1L)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .build();

        Tag tag1 = Tag.builder()
                .name("tag1")
                .posts(Set.of(post1,post2))
                .build();

        Tag tag2 = Tag.builder()
                .name("tag2")
                .posts(Set.of(post1))
                .build();

        given(tagRepository.findTagsWihPosts()).willReturn(List.of(tag1, tag2));

        List<TagResponseDto> result = tagService.getTags();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getTagsWithPosts_WhenListIsEmpty() {
        given(tagRepository.findTagsWihPosts()).willReturn(Collections.emptyList());

        List<TagResponseDto> result = tagService.getTags();
        assertThat(result).isNotNull();
        assertThat(result.size()).isZero();
    }

}
