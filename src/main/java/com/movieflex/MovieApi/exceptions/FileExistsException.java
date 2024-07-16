package com.movieflex.MovieApi.exceptions;

public class FileExistsException extends RuntimeException{

    public FileExistsException(String message) {
        super(message);
    }
}
