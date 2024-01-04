package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.entity.Vote;
import com.example.demo.enumeration.EVoteType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VoteRepositoryTests {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setup() {

        user = User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .savedPosts(new HashSet<>())
                .build();
        userRepository.save(user);

        post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .author(user)
                .build();
        postRepository.save(post1);

        post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .author(user)
                .build();
        postRepository.save(post2);

        Vote vote1 = Vote.builder()
                .user(user)
                .post(post1)
                .type(EVoteType.valueOf("UPVOTE"))
                .build();
        voteRepository.save(vote1);

        Vote vote2 = Vote.builder()
                .user(user)
                .post(post2)
                .type(EVoteType.valueOf("DOWNVOTE"))
                .build();
        voteRepository.save(vote2);
    }

    @Test
    void testFindByUserAndPost_Success() {
        Vote retrievedVote = voteRepository.findByUserIdAndPostId(user.getId(), post2.getId());

        assertThat(retrievedVote).isNotNull();
        assertThat(retrievedVote.getType()).isEqualTo(EVoteType.valueOf("DOWNVOTE"));
    }

    @Test
    void testSaveVote() {
        Post post3 = Post.builder()
                .title("Post 3")
                .content("Content 3")
                .author(user)
                .build();
        postRepository.save(post3);

        Vote newVote = Vote.builder()
                .user(user)
                .post(post3)
                .type(EVoteType.valueOf("DOWNVOTE"))
                .build();
        voteRepository.save(newVote);

        Vote retrievedVote = voteRepository.findByUserIdAndPostId(user.getId(), post3.getId());

        assertThat(retrievedVote.getId()).isGreaterThan(0);
        assertThat(voteRepository.count()).isGreaterThan(0);
        assertThat(retrievedVote.getType()).isEqualTo(EVoteType.valueOf("DOWNVOTE"));
    }

    @Test
    void testFindUserUpVotedPosts() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = voteRepository.findUserUpVotedPosts(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent()).isNotNull();
        assertThat(postsPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testFindUserDownVotedPosts() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = voteRepository.findUserDownVotedPosts(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
        assertThat(postsPage.getContent()).isNotNull();
        assertThat(postsPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testFindAllByPost() {
        List<Vote> votes = voteRepository.findAllByPostId(post1.getId());

        assertThat(votes).isNotNull();
        assertThat(votes.size()).isEqualTo(1);
    }

    @Test
    void testDeleteByUserAndPost() {
        voteRepository.deleteByUserIdAndPostId(user.getId(), post1.getId());

        Vote deletedVote = voteRepository.findByUserIdAndPostId(user.getId(), post1.getId());

        assertThat(deletedVote).isNull();
    }
}
