package com.example.demo.repository;

import com.example.demo.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteTypeRepository extends JpaRepository<VoteType,Long> {
}
