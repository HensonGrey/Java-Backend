package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.impl.AuthServiceImpl;
import com.example.demo.dtos.AuthResponseDto;
import com.example.demo.dtos.LoginDto;
import com.example.demo.dtos.RegisterDto;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserIsNotAdminException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Roles;
import com.example.demo.models.UserEntity;
import com.example.demo.Security.JWTGenerator;

@SpringBootTest
class AuthServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthServiceImpl authService;

	@Mock
	private Authentication authentication;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JWTGenerator jwtGenerator;

	@Mock
	private SecurityContext securityContext;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testLogin_Success() {

		LoginDto loginDto = new LoginDto();
		loginDto.setUsername("marti");
		loginDto.setPassword("123");

		Authentication mockAuth = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);

		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(mockAuth);

		when(jwtGenerator.generateToken(mockAuth)).thenReturn("mocked-jwt-token");

		AuthResponseDto response = authService.login(loginDto);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

		assertEquals("mocked-jwt-token", response.getAccessToken());
	}

	@Test
	public void testRegisterAdmin_UserAlreadyExists() throws Exception {
		RegisterDto registerDto = new RegisterDto();

		registerDto.setUsername("marti");
		registerDto.setPassword("123");

		when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);

		Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
			authService.registerAdmin(registerDto);
		});

		assertEquals("User already exists!", exception.getMessage());
	}

	@Test
	public void testRegisterAdmin_Success() throws Exception {
		RegisterDto registerDto = new RegisterDto();
		registerDto.setUsername("marti");
		registerDto.setPassword("123");

		when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);

		Roles adminRole = new Roles();
		adminRole.setName("ADMIN");
		when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

		when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

		authService.registerAdmin(registerDto);

		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	public void testEdit_Success() {
		UserEntity existingUser = new UserEntity();
		existingUser.setId(1);
		existingUser.setUsername("marti");
		existingUser.setPassword("123");
		UserEntity newUserData = new UserEntity();
		String newPass = "palec123";
		String newUsername = "Kolio";
		newUserData.setUsername(newUsername);
		newUserData.setPassword(newPass);

		when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));

		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

		when(userRepository.save(existingUser)).thenReturn(newUserData);

		UserEntity updatedUser = authService.edit(1, newUserData, authentication);

		assertEquals(newUsername, updatedUser.getUsername());
		assertEquals(newPass, updatedUser.getPassword());
		verify(userRepository, times(1)).findById(1);
		verify(passwordEncoder, times(1)).encode(newPass);
		verify(userRepository, times(1)).save(existingUser);
	}

	@Test
	public void testEdit_UserNotFound() {

		UserEntity newUserData = new UserEntity();
		newUserData.setId(1);
		String newPass = "marti";
		String newUsername = "123";
		newUserData.setUsername(newUsername);
		newUserData.setPassword(newPass);

		when(userRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(UserNotFoundException.class, () -> {
			authService.edit(1, newUserData, authentication);
		});

		assertEquals("User not found", exception.getMessage());
		verify(userRepository, times(1)).findById(1);
	}

	@Test
	public void testDeleteUserById_Admin() throws Exception {
		UserEntity userToDelete = new UserEntity();
		userToDelete.setId(1);
		userToDelete.setUsername("marti");
		userToDelete.setPassword("123");

		when(userRepository.findById(1)).thenReturn(Optional.of(userToDelete));

		Answer<Collection<GrantedAuthority>> authoritiesAnswer = invocation -> {
			return Arrays.asList(new SimpleGrantedAuthority("ADMIN"));
		};
		when(authentication.getAuthorities()).thenAnswer(authoritiesAnswer);

		authService.deleteUserById(1, authentication);

		verify(userRepository, times(1)).delete(userToDelete);
	}

	@Test
	public void testDeleteUserById_NotAdmin() throws Exception {
		// Prepare mock user
		UserEntity userToDelete = new UserEntity();
		userToDelete.setId(1);
		userToDelete.setUsername("marti");
		userToDelete.setPassword("123");

		when(userRepository.findById(1)).thenReturn(Optional.of(userToDelete));

		Answer<Collection<GrantedAuthority>> authoritiesAnswer = invocation -> {
			return Arrays.asList(new SimpleGrantedAuthority("USER"));
		};
		when(authentication.getAuthorities()).thenAnswer(authoritiesAnswer);

		Exception exception = assertThrows(UserIsNotAdminException.class, () -> {
			authService.deleteUserById(1, authentication);
		});

		assertEquals("User is not admin!", exception.getMessage());
		verify(userRepository, times(0)).delete(userToDelete);
	}

}
