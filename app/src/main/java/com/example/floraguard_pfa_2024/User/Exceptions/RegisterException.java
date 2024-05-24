package com.example.floraguard_pfa_2024.User.Exceptions;

public class RegisterException extends Exception {


    public RegisterExceptionCause cause;
    private final String message;
    public RegisterException(RegisterExceptionCause cause, String message) {
        super();

        this.cause = cause;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
