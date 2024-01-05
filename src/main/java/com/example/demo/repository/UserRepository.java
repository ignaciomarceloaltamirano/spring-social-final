package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT p FROM User u JOIN u.savedPosts p WHERE u.id = :userId")
    Page<Post> findSavedPostsById(Long userId, PageRequest p);
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM User u JOIN u.savedPosts p WHERE u.id = :userId AND p.id = :postId")
    boolean isPostSavedByUser(@Param("userId") Long userId, @Param("postId") Long postId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
