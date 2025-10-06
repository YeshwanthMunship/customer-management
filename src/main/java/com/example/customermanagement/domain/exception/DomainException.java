package com.example.customermanagement.domain.exception;

public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getErrorCode();

    public Object getErrorContext() {
        return null;
    }
}
