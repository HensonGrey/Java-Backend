package com.example.demo;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Optional;
import com.example.demo.Repository.UserRepository;
import com.example.demo.exceptions.CarNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.impl.*;

public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessTransaction_Success() throws Exception {
        ArrayList<Long> cart = new ArrayList<>();
        cart.add(1L);
        cart.add(2L);

        UserEntity mockUser = new UserEntity();
        mockUser.setUsername("testUser");

        Car car1 = new Car();
        car1.setId(1);
        car1.setPrice(10000);

        Car car2 = new Car();
        car2.setId(2);
        car2.setPrice(15000);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car1));
        when(carRepository.findById(2L)).thenReturn(Optional.of(car2));

        transactionService.processTransaction(cart, authentication);

        verify(transactionRepository, times(1)).save(argThat(transaction -> {
            assertEquals(mockUser, transaction.getUser());
            assertEquals(25000.0, transaction.getPayment());
            assertNotNull(transaction.getExactTime());
            assertNotNull(transaction.getUuid());
            return true;
        }));
    }

    @Test
    public void testProcessTransaction_UserNotFound() {
        ArrayList<Long> cart = new ArrayList<>();
        cart.add(1L);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonExistentUser");
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> transactionService.processTransaction(cart, authentication));

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    public void testProcessTransaction_CarNotFound() {
        ArrayList<Long> cart = new ArrayList<>();
        cart.add(1L);

        UserEntity mockUser = new UserEntity();
        mockUser.setUsername("testUser");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CarNotFoundException.class,
                () -> transactionService.processTransaction(cart, authentication));

        assertEquals("Shouldnt even be possible???", exception.getMessage());
    }
}