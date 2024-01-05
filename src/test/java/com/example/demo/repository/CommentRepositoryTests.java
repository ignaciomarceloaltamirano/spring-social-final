package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTests {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User author;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setup() {
        author = User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(author);

        post = new Post();
        postRepository.save(post);

        comment = Comment.builder()
                .text("Comment 1")
                .author(author)
                .post(post)
                .build();
        commentRepository.save(comment);
    }

    @Test
    void testSaveComment() {
        Comment newComment = Comment.builder()
                .text("Hello")
                .build();
        commentRepository.save(newComment);

        Comment retrievedComment = commentRepository.findById(newComment.getId()).orElseThrow();

        assertThat(retrievedComment).isNotNull();
        assertThat(retrievedComment.getId()).isGreaterThan(0);
        assertThat(retrievedComment.getText()).isEqualTo("Hello");
        assertThat(commentRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindAllByPost() {
        Comment comment2 = Comment.builder()
                .text("Comment 2")
                .author(author)
                .post(post)
                .build();
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findAllByPost(post);

        assertThat(comments).isNotNull();
        assertThat(comments.get(0).getText()).isEqualTo("Comment 1");
        assertThat(comments.get(1).getText()).isEqualTo("Comment 2");
        assertThat(comments.size()).isEqualTo(2);
    }

    @Test
    void testFindById_Success() {
        Comment retrievedComment = commentRepository.findById(comment.getId()).orElseThrow();

        assertThat(retrievedComment).isNotNull();
        assertThat(retrievedComment.getText()).isEqualTo("Comment 1");
    }

    @Test
    void testFindById_WhenCommentNotFound_ThrowsResourceNotFoundException() {
        Long nonExistentId = -1L;

        assertThrows(ResourceNotFoundException.class, () -> commentRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found")));
    }

    @Test
    void testUpdateComment() {
        comment.setText("Old comment text");

        Comment retrievedComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNotNull(retrievedComment);

        retrievedComment.setText("New comment text");
        commentRepository.save(retrievedComment);

        Comment updatedComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNotNull(updatedComment);
        assertEquals("New comment text", updatedComment.getText());
    }

    @Test
    void testDeleteById() {
        Comment savedComment = commentRepository.save(comment);
        long commentsCount = commentRepository.count();

        commentRepository.deleteById(savedComment.getId());

        assertThat(commentRepository.count()).isEqualTo(commentsCount - 1);
        assertThat(commentRepository.findById(savedComment.getId())).isEqualTo(Optional.empty());
    }
}