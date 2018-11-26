package br.com.zup.restapi.projectapi.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception{
    private final HttpStatus statusCode;
    private final String message;

    public CustomException(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
