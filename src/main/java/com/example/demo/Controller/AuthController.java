package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Service.AuthService;
import com.example.demo.dtos.AuthResponseDto;
import com.example.demo.dtos.LoginDto;
import com.example.demo.dtos.RegisterDto;
import com.example.demo.models.UserEntity;
import java.util.List;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("get-all")
    public ResponseEntity<List<UserEntity>> getAll(Authentication authentication) throws Exception{
        return new ResponseEntity<>(authService.getAll(authentication), HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) throws Exception {
        authService.register(registerDto);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterDto registerDto) throws Exception{
        authService.registerAdmin(registerDto);
        return new ResponseEntity<>("Admin profile was successfully created! (Use with caution)", HttpStatus.CREATED);
    }

    @PutMapping("user/edit/{user_id}")
    public ResponseEntity<UserEntity> edit(
        @RequestBody UserEntity new_user_data, 
        @PathVariable Integer user_id,
        Authentication authentication
    ) throws Exception {
        
        return new ResponseEntity<>(authService.edit(user_id, new_user_data, authentication), HttpStatus.OK);
    }

    @DeleteMapping("delete/user/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, Authentication authentication) throws Exception{
        authService.deleteUserById(id, authentication);
        return new ResponseEntity<>("User with id:" + id + " was deleted successfully!", HttpStatus.OK);
    }
}