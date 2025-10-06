package com.example.customermanagement.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends DomainException {
    
    private final UUID customerId;
    
    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found with ID: " + customerId);
        this.customerId = customerId;
    }
    
    public CustomerNotFoundException(UUID customerId, String message) {
        super(message);
        this.customerId = customerId;
    }
    
    public CustomerNotFoundException(UUID customerId, String message, Throwable cause) {
        super(message, cause);
        this.customerId = customerId;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    @Override
    public String getErrorCode() {
        return "CUSTOMER_NOT_FOUND";
    }
    
    @Override
    public Object getErrorContext() {
        return customerId;
    }
}
