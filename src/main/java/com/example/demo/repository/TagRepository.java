package com.example.demo.repository;

import com.example.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByName(String name);
    @Query("SELECT DISTINCT t FROM Tag t JOIN FETCH t.posts WHERE SIZE(t.posts) > 0")
    List<Tag> findTagsWihPosts();
}
