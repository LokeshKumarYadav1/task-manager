package com.example.Task.Manager.Application.exceptionHandler;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message) {

        super(message);

    }

}
