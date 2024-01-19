package com.example.demo.repository;

import com.example.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    @Query(value = """
      SELECT t from Token t INNER JOIN User u\s
      ON t.user.id = u.id\s
      WHERE u.id = :id AND (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokensByUser(Long id);
    Optional<Token> findByToken(String token);
}
