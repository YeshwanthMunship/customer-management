package com.example.customermanagement.domain.exception;

public class InvalidAddressException extends DomainException {
    
    private final String field;
    private final String value;
    
    public InvalidAddressException(String field, String value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public InvalidAddressException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_ADDRESS";
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public Object getErrorContext() {
        return field != null ? new FieldContext(field, value) : null;
    }
    
    public record FieldContext(String field, String value) {
    }
    
    public static InvalidAddressException nullOrEmpty(String field) {
        return new InvalidAddressException(field, null, String.format("Address %s cannot be null or empty", field));
    }
}