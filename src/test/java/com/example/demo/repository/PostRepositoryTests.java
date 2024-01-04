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

import java.util.List;
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
    private Post post2;
    private Community community;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(user);

        community = Community.builder()
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
                .community(community)
                .tags(Set.of(tag1, tag2))
                .build();

        post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .community(community)
                .author(user)
                .build();
    }

    @Test
    void testSavePost() {
        postRepository.save(post1);

        Post retrievedPost = postRepository.findById(post1.getId()).orElseThrow();

        assertThat(retrievedPost).isNotNull();
        assertThat(retrievedPost.getId()).isGreaterThan(0);
        assertThat(postRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        postRepository.save(post1);

        Post retrievedPost = postRepository.findById(post1.getId()).orElseThrow();

        assertThat(retrievedPost).isNotNull();
    }

    @Test
    void testFindById_WhenPostNotFound_ThrowsResourceNotFoundException() {
        Long nonExistentId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> postRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found")));
    }

    @Test
    void testFindAllByTagsName() {
        postRepository.save(post1);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByTagsName("tag1", pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent()).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllInUserSubscribedCommunities() {
        postRepository.save(post1);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findPostsInUserSubscribedCommunities(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByCommunity() {
        postRepository.save(post1);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByCommunityId(community.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByAuthor() {
        postRepository.saveAll(List.of(post1,post2));

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByAuthorId(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllSavedPostsByUser() {
        postRepository.save(post1);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = postRepository.findAllByAuthorId(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testDeletePost() {
        postRepository.save(post1);
        long postsCount = postRepository.count();

        postRepository.delete(post1);

        assertThat(postRepository.count()).isEqualTo(postsCount - 1);
        assertThat(postRepository.findById(post1.getId())).isEqualTo(Optional.empty());
    }

    @Test
    void testDeleteAllByCommunity() {
        postRepository.saveAll(List.of(post1,post2));

        postRepository.deleteAllByCommunityId(community.getId());

        assertThat(postRepository.findById(post1.getId())).isEqualTo(Optional.empty());
        assertThat(postRepository.findById(post2.getId())).isEqualTo(Optional.empty());
    }
}
