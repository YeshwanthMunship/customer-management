package com.example.customermanagement.domain.exception;

public class InvalidAddressException extends DomainException {
    
    private final String field;
    private final String value;
    
    public InvalidAddressException(String field, String value, String message) {
        super(String.format("Invalid address field '%s' with value '%s': %s", field, value, message));
        this.field = field;
        this.value = value;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_ADDRESS_FIELD";
    }
    
    public static InvalidAddressException nullOrEmpty(String field) {
        return new InvalidAddressException(field, "null/empty", "cannot be null or empty");
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
}
