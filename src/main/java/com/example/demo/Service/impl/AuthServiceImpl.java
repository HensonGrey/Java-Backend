package com.example.demo.Service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.JWTGenerator;
import com.example.demo.Service.AuthService;
import com.example.demo.dtos.AuthResponseDto;
import com.example.demo.dtos.LoginDto;
import com.example.demo.dtos.RegisterDto;
import com.example.demo.exceptions.UnauthorizedEdittingException;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserIsNotAdminException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Roles;
import com.example.demo.models.UserEntity;

@Service
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            JWTGenerator jwtGenerator){

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public List<UserEntity> getAll(Authentication authentication) throws Exception {
        boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if(!isAdmin){
            throw new UserIsNotAdminException("User is not Admin!");
        }
        
        return userRepository.findAll();
    }

    @Override
    public AuthResponseDto login(LoginDto loginDto) {
                Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        return new AuthResponseDto(token);
    }

    @Override
    public void register(RegisterDto registerDto) throws Exception {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException("User already exists!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Roles roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);
    }

    @Override
    public void registerAdmin(RegisterDto registerDto) throws Exception {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException("User already exists!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Roles roles = roleRepository.findByName("ADMIN").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);
    }

    @Override
    public UserEntity edit(Integer id, UserEntity new_user_data, Authentication authentication) {
        // Get the currently authenticated username
        // String currentUsername = authentication.getName();
        
        // Find the user
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Verify the authenticated user is editing their own profile
        // if (!user.getUsername().equals(currentUsername)) {
        //     throw new UnauthorizedEdittingException("Unauthorized to edit this profile!");
        // }

        user.setUsername(new_user_data.getUsername());
        user.setPassword(passwordEncoder.encode(new_user_data.getPassword()));
    
        UserEntity newUserData = userRepository.save(user);
        return newUserData;
    }

    @Override
    public void deleteUserById(Integer id, Authentication authentication) throws Exception{
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found exception!"));

        boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if(!isAdmin){
           throw new UserIsNotAdminException("User is not admin!");
        }

        user.getRoles().clear();
        userRepository.delete(user);
    }
    
}
