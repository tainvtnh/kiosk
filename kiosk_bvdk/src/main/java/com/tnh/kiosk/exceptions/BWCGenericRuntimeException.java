package com.tnh.kiosk.exceptions;

public class BWCGenericRuntimeException extends BasewareCoreException {

    public BWCGenericRuntimeException(String message) {
        super(message, 500);
    }

    public BWCGenericRuntimeException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
