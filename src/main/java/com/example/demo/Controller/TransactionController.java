package com.example.demo.Controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.TransactionService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payment/")
public class TransactionController {
    
    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("process")
    public ResponseEntity<String> processTransaction(
                                                    @RequestBody ArrayList<Long> cart, 
                                                    Authentication authentication) 
                                                    throws Exception{
        transactionService.processTransaction(cart, authentication);

        return new ResponseEntity<>("Payment was successfully processed!", HttpStatus.OK);
    }
}
