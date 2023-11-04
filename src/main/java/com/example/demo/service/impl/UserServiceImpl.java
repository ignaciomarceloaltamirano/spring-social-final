package com.example.demo.service.impl;

import com.example.demo.dto.response.PageDto;
import com.example.demo.dto.response.UserProfileResponseDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserProfileResponseDto> getUsers() {
        List<User> users=userRepository.findAll();

        return users.stream().map(user -> modelMapper.map(user,UserProfileResponseDto.class)).toList();
    }

    @Override
    public UserProfileResponseDto getUser(String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return modelMapper.map(user,UserProfileResponseDto.class);
    }
}
