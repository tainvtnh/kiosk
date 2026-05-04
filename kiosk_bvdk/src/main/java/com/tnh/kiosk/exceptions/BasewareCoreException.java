package com.tnh.kiosk.exceptions;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class BasewareCoreException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    int errorCode;

    public BasewareCoreException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BasewareCoreException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
