package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post1;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("user1")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(user);

        post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .author(user)
                .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .author(user)
                .build();
        postRepository.save(post2);

        user.setSavedPosts(Set.of(post1, post2));
    }

    @Test
    void testSaveUser() {
        User newUser = User.builder()
                .username("new")
                .email("newtest@test.com")
                .password("test")
                .build();
        userRepository.save(newUser);

        User retrievedUser = userRepository.findById(newUser.getId()).get();

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isGreaterThan(0);
        assertThat(userRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        User retrievedUser = userRepository.findById(user.getId()).get();

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindById_WhenUserNotFound_ThrowsResourceNotFoundException() {
        Long nonExistentId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> userRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Test
    void testExistsByUsername_WhenUserExists_ReturnsTrue() {
        boolean exists = userRepository.existsByUsername(user.getUsername());

        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_WhenUserDoesNotExist_ReturnsFalse() {
        String username = "nonexistentuser";

        boolean exists = userRepository.existsByUsername(username);

        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_WhenEmailExists_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail(user.getEmail());

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_WhenEmailDoesNotExist_ReturnsFalse() {
        String email = "nonexistent@test.com";

        boolean exists = userRepository.existsByEmail(email);

        assertFalse(exists);
    }

    @Test
    void testFindByUsername() {
        User retrievedUser = userRepository.findByUsername(user.getUsername());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindUserSavedPosts() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Post> postsPage = userRepository.findSavedPostsById(user.getId(), pageRequest);

        assertThat(postsPage).isNotNull();
    }

    @Test
    void testIsPostSavedByUser_WhenPostIsSaved_ReturnsTrue() {
        boolean isSaved = userRepository.isPostSavedByUser(user.getId(), post1.getId());

        assertTrue(isSaved);
    }

    @Test
    void testIsPostSavedByUser_WhenPostIsNotSaved_ReturnsFalse() {
        boolean isSaved = userRepository.isPostSavedByUser(user.getId(), -1L);

        assertFalse(isSaved);
    }

    @Test
    void testFindAll() {
        User user2 = User.builder()
                .username("user2")
                .email("test2@test.com")
                .password("test")
                .build();
        userRepository.save(user2);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<User> usersPage = userRepository.findAll(pageRequest);

        assertThat(usersPage).isNotNull();
    }
}
