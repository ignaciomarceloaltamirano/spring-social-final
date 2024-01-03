package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.CommentVote;
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
//import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentVoteRepositoryTests {
    @Autowired
    private CommentVoteRepository commentVoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Comment comment;
    private CommentVote commentVote;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("author")
                .email("test@test.com")
                .password("test")
                .build();
        userRepository.save(user);

        comment = commentRepository.save(new Comment());

        commentVote = CommentVote.builder().user(user).comment(comment).build();
        commentVoteRepository.save(commentVote);
    }

    @Test
    void testSaveCommentVote() {
        CommentVote newCommentVote = CommentVote.builder()
                .user(user)
                .build();

        commentVoteRepository.save(newCommentVote);

        CommentVote retrievedCommentVote = commentVoteRepository.findById(newCommentVote.getId()).orElseThrow();

        assertThat(retrievedCommentVote).isNotNull();
        assertThat(retrievedCommentVote.getId()).isGreaterThan(0);
        assertThat(commentVoteRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindByUserAndComment() {
        CommentVote retrievedCommentVote = commentVoteRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).orElseThrow();

        assertThat(retrievedCommentVote).isNotNull();
        assertThat(retrievedCommentVote.getUser()).isEqualTo(user);
        assertThat(retrievedCommentVote.getComment()).isEqualTo(comment);
    }

    @Test
    void testFindAllByComment() {
        CommentVote commentVote2 = CommentVote.builder()
                .comment(comment)
                .user(user)
                .build();
        commentVoteRepository.save(commentVote2);

        List<CommentVote> commentVotes = commentVoteRepository.findAllByComment(comment);

        assertThat(commentVotes).isNotNull();
        assertThat(commentVotes.size()).isEqualTo(2);
    }

    @Test
    void testDeleteByUserAndComment() {
        commentVoteRepository.save(commentVote);

        long commentVotesCount = commentVoteRepository.count();

        commentVoteRepository.deleteByUserIdAndCommentId(commentVote.getUser().getId(), commentVote.getComment().getId());

        assertThat(commentVoteRepository.count()).isEqualTo(commentVotesCount - 1);
        assertThat(commentVoteRepository.findById(commentVote.getId())).isEqualTo(Optional.empty());
    }
}
