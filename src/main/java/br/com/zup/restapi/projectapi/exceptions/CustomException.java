package br.com.zup.restapi.projectapi.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception{
    private HttpStatus statusCode;
    private String message;

    public CustomException(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
