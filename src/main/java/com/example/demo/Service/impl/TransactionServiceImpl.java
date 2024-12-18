package com.example.demo.Service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CarRepository;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.TransactionService;
import com.example.demo.exceptions.CarNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Car;
import com.example.demo.models.Transaction;
import com.example.demo.models.UserEntity;

@Service
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private CarRepository carRepository;

    @Autowired
    public TransactionServiceImpl(
                                    TransactionRepository transactionRepository, 
                                    UserRepository userRepository,
                                    CarRepository carRepository){
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Override
    public void processTransaction(ArrayList<Long> cart, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserEntity user = userRepository
                                        .findByUsername(username)
                                        .orElseThrow(() -> new UserNotFoundException("User not found!"));
        double price = 0;
        for (Long product_id : cart) {
            Car car = carRepository
                                        .findById(product_id)
                                        .orElseThrow(() -> new CarNotFoundException("Shouldnt even be possible???"));
            price += car.getPrice();
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPayment(price);
        transaction.setExactTime(LocalDateTime.now());
        transaction.setUuid(UUID.randomUUID());

        transactionRepository.save(transaction);
    }    
}
