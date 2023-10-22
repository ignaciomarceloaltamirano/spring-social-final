package com.example.demo.repository;

import com.example.demo.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community,Long> {
    Page<Community> findAllByNameContaining(String query, PageRequest p);
    Page<Community> findAll(Pageable p);
}
