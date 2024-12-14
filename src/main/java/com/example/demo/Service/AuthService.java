package com.example.demo.Service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.demo.dtos.AuthResponseDto;
import com.example.demo.dtos.LoginDto;
import com.example.demo.dtos.RegisterDto;
import com.example.demo.models.UserEntity;

public interface AuthService {
    List<UserEntity> getAll(Authentication authentication) throws Exception;
    AuthResponseDto login(LoginDto loginDto);
    void register(RegisterDto registerDto) throws Exception;
    UserEntity edit(Integer id, UserEntity new_user_data, Authentication authentication) throws Exception;
    void deleteUserById(Integer id, Authentication authentication) throws Exception;
}
