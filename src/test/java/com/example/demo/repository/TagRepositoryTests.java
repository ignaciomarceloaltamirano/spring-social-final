package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TagRepositoryTests {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    private Tag tag1;

    @BeforeEach
    void setup(){
        tag1 = Tag.builder()
                .name("test")
                .build();
        tagRepository.save(tag1);
    }

    @Test
    void testFindByName(){
        Tag retrievedTag=tagRepository.findByName("test").get();

        assertThat(retrievedTag).isNotNull();
        assertThat(retrievedTag.getName()).isEqualTo("test");
    }

    @Test
    void testSaveAll(){
        Tag tag2 = Tag.builder()
                .name("test 2")
                .build();

        Tag tag3 = Tag.builder()
                .name("test 3")
                .build();

        List<Tag> tags = new ArrayList<>();
        tags.add(tag2);
        tags.add(tag3);
        List<Tag> savedTags=tagRepository.saveAll(tags);

        assertThat(savedTags.size()).isEqualTo(2);
        assertThat(savedTags.get(0).getName()).isEqualTo("test 2");
        assertThat(savedTags.get(1).getName()).isEqualTo("test 3");
    }

    @Test
    void testFindTagsWithPosts(){
        Post post=Post.builder()
                .tags(Set.of(tag1))
                .build();
        postRepository.save(post);

        List<Tag> tags=tagRepository.findTagsWihPosts();

        assertThat(tags).isNotNull();
        assertThat(tags.size()).isGreaterThan(0);
    }
}
