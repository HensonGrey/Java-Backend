package com.example.demo.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public UserAlreadyExistsException(String message){
        super(message);
    }
}
