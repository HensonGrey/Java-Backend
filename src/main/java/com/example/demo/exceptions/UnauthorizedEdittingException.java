package com.example.demo.exceptions;

public class UnauthorizedEdittingException extends RuntimeException{
    public UnauthorizedEdittingException(String message){
        super(message);
    }
}
