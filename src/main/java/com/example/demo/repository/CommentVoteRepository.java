package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.CommentVote;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<CommentVote,Long> {
    Optional<CommentVote> findByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByUserAndComment(User user, Comment comment);

    List<CommentVote> findAllByComment(Comment comment);
}
