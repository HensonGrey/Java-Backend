package com.example.demo.exceptions;

public class UserIsNotAdminException extends RuntimeException {
    public UserIsNotAdminException(String message){
        super(message);
    }
}
