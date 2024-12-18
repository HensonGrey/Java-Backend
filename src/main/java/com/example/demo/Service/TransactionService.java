package com.example.demo.Service;
import java.util.ArrayList;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    void processTransaction(ArrayList<Long> cart, Authentication authentication) throws Exception;
}