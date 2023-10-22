package com.example.demo.dto.response;

import com.example.demo.entity.Community;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private User author;
    private Community community;
    private Set<Tag> tags = new HashSet<>();
    private Set<User> users;
}
