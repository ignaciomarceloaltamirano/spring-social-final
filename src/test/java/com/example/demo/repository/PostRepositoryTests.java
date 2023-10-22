package com.example.demo.repository;

import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post1;
    private Community community;

    @BeforeEach
    void setup() {
        user=User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(user);

        community=Community.builder()
                .creator(user)
                .name("testcommunity")
                .build();
        communityRepository.save(community);

        Subscription subscription = Subscription.builder()
                .user(user)
                .community(community)
                .build();
        subscriptionRepository.save(subscription);

        Tag tag1 = Tag.builder()
                .name("tag1")
                .build();
        tagRepository.save(tag1);

        Tag tag2 = Tag.builder()
                .name("tag2")
                .build();
        tagRepository.save(tag2);

        post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .author(user)
                .tags(Set.of(tag1, tag2))
                .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .author(user)
                .build();
        postRepository.save(post2);
    }

    @Test
    void testSavePost() {
        Post newPost = Post.builder()
                .title("Hello")
                .build();
        postRepository.save(newPost);
        Post retrievedPost = postRepository.findById(newPost.getId()).get();

        assertThat(retrievedPost).isNotNull();
        assertThat(retrievedPost.getId()).isGreaterThan(0);
        assertThat(postRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        Post retrievedPost = postRepository.findById(post1.getId()).get();

        assertThat(retrievedPost).isNotNull();
    }

    @Test
    void testFindById_WhenPostNotFound_ThrowsResourceNotFoundException() {
        Long nonExistentId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> postRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found")));
    }

    @Test
    void testFindAll() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAll(pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent()).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByTagsName() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByTagsName("tag1", pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent()).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllInUserSubscribedCommunities() {
        post1.setCommunity(community);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findPostsInUserSubscribedCommunities(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByCommunity() {
        post1.setCommunity(community);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByCommunityId(community.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByAuthor() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByAuthorId(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllSavedPostsByUser() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByAuthorId(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testDeletePost() {
        long postsCount = postRepository.count();

        postRepository.delete(post1);

        assertThat(postRepository.count()).isEqualTo(postsCount - 1);
        assertThat(postRepository.findById(post1.getId())).isEqualTo(Optional.empty());
    }
}
