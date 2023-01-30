package com.densoft.sec.errorhandling;

public class APIException extends RuntimeException {

    public APIException(String message) {
        super(message);
    }
}
