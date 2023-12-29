package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.enumeration.ERole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void cleanup() {
        roleRepository.deleteAll();
    }

    @Test
    void testSaveRole() {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role userRole = Role.builder()
                    .name(ERole.ROLE_USER)
                    .build();
            roleRepository.save(userRole);
        }
        assertThat(roleRepository.count()).isEqualTo(1);
    }

    @Test
    void testFindByName() {
        Role userRole = Role.builder()
                .name(ERole.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        Role retrievedUserRole = roleRepository.findByName(ERole.ROLE_USER).get();
        assertThat(retrievedUserRole.getName()).isEqualTo(ERole.ROLE_USER);
    }
}
