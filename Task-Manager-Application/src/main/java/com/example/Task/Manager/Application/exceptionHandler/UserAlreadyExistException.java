package com.example.Task.Manager.Application.exceptionHandler;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(String message) {

        super(message);

    }

}
