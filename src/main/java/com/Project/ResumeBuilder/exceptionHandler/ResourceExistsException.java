package com.Project.ResumeBuilder.exceptionHandler;

public class ResourceExistsException extends RuntimeException{

    public ResourceExistsException(String message){
        super(message);
    }
}
