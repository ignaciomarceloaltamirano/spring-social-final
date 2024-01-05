package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "characters2")
public class Character2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id2;
    private String name2;
}
