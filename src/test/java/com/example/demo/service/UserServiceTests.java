package com.example.demo.service;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUsers(){
        Page<User> pageRequest=new PageImpl<>(Collections.singletonList(new User()));

        given(userRepository.findAll(any(PageRequest.class))).willReturn(pageRequest);

        PageDto<UserResponseDto> result=userService.getUsers(1);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(PageDto.class);
    }
}
