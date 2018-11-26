package br.com.zup.restapi.projectapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResponseEntity customExceptionHandler(CustomException e) {
        return new ResponseEntity<>( e.getMessage(), e.getStatusCode());
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity customExceptionHandler(RuntimeException e) {
        return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}