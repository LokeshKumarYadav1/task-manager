package com.example.Task.Manager.Application.exceptionHandler;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String message) {

        super(message);

    }

}
