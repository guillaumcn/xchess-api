package com.xchess.engine.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ChessEngineWorkerExecutionException extends RuntimeException {
    public ChessEngineWorkerExecutionException(Throwable cause) {
        super(cause);
    }
}
