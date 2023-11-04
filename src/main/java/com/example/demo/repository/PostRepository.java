package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByTagsName(String tagName, PageRequest p);
    Page<Post>findAllByCommunityId(Long communityId,PageRequest p);
    Page<Post>findAllByAuthorId(Long authorId,PageRequest p);
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:query% OR p.author.username LIKE %:query%")
    Page<Post> findPostsByTitleOrAuthorContaining(@Param("query") String query, PageRequest pageable);
    @Query("SELECT p FROM Post p WHERE p.community IN (SELECT s.community FROM Subscription s WHERE s.user.id = :userId)")
    Page<Post> findPostsInUserSubscribedCommunities(@Param("userId") Long userId, PageRequest p);
}
