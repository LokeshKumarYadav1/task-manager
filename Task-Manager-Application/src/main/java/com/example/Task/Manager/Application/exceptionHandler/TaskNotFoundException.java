package com.example.Task.Manager.Application.exceptionHandler;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(String message) {

        super(message);

    }

}
