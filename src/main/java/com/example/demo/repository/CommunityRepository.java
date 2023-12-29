package com.example.demo.repository;

import com.example.demo.dto.response.CommunityResponseDto;
import com.example.demo.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community,Long> {
    boolean existsByName(String name);
    Optional<Community> findByName(String name);
}
