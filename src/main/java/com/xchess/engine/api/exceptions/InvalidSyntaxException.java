package com.xchess.engine.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSyntaxException extends RuntimeException {
    public InvalidSyntaxException(Throwable cause) {
        super(cause);
    }
}
