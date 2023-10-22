package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.enumeration.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(ERole name);
    Optional<Role> findByName(ERole name);
}
