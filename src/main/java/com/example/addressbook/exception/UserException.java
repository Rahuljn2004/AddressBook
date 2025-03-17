package com.example.addressbook.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@AllArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserException extends Exception {
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
