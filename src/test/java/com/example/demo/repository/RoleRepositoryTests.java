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

        if (roleRepository.findByName(ERole.ROLE_MOD).isEmpty()) {
            Role modRole = Role.builder()
                    .name(ERole.ROLE_MOD)
                    .build();
            roleRepository.save(modRole);
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = Role.builder()
                    .name(ERole.ROLE_ADMIN)
                    .build();
            roleRepository.save(adminRole);
        }
        assertThat(roleRepository.count()).isEqualTo(3);
    }

    @Test
    void testFindByName() {
        Role userRole = Role.builder()
                .name(ERole.ROLE_USER)
                .build();
        roleRepository.save(userRole);

        Role modRole = Role.builder()
                .name(ERole.ROLE_MOD)
                .build();
        roleRepository.save(modRole);

        Role adminRole = Role.builder()
                .name(ERole.ROLE_ADMIN)
                .build();
        roleRepository.save(adminRole);

        Role retrievedUserRole = roleRepository.findByName(ERole.ROLE_USER).get();
        assertThat(retrievedUserRole.getName()).isEqualTo(ERole.ROLE_USER);

        Role retrievedModRole = roleRepository.findByName(ERole.ROLE_MOD).get();
        assertThat(retrievedModRole.getName()).isEqualTo(ERole.ROLE_MOD);

        Role retrievedAdminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
        assertThat(retrievedAdminRole.getName()).isEqualTo(ERole.ROLE_ADMIN);
    }
}
