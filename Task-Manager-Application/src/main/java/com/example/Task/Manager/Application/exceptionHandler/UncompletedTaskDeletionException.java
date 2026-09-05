package com.example.Task.Manager.Application.exceptionHandler;

public class UncompletedTaskDeletionException extends RuntimeException{

    public UncompletedTaskDeletionException(String message) {

        super(message);

    }

}
