package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote,Long> {
    Vote findByUserIdAndPostId(Long userId,Long postId);
    void deleteByUserIdAndPostId(Long id, Long postId);
    List<Vote> findAllByPostId(Long postId);
    @Query("SELECT v.post FROM Vote v WHERE v.user.id = :userId AND v.type = com.example.demo.enumeration.EVoteType.UPVOTE")
    Page<Post> findUserUpVotedPosts(@Param("userId") Long userId, PageRequest p);
    @Query("SELECT v.post FROM Vote v WHERE v.user.id = :userId AND v.type = com.example.demo.enumeration.EVoteType.DOWNVOTE")
    Page<Post> findUserDownVotedPosts(@Param("userId") Long userId,PageRequest p);
}
