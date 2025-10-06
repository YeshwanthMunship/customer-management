package com.example.customermanagement.domain.exception;

public class InvalidCustomerDataException extends DomainException {

    private final String field;
    private final Object value;

    public InvalidCustomerDataException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public InvalidCustomerDataException(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public InvalidCustomerDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public static InvalidCustomerDataException nullCustomer() {
        return new InvalidCustomerDataException("customer", null, "Customer cannot be null");
    }


    public static InvalidCustomerDataException nullCustomerId() {
        return new InvalidCustomerDataException("customerId", null, "Customer ID cannot be null");
    }

    public static InvalidCustomerDataException invalidPagination(String field, Object value, String message) {
        return new InvalidCustomerDataException(field, value, message);
    }
    
    public static InvalidCustomerDataException nullOrEmptyField(String field, String value) {
        return new InvalidCustomerDataException(field, value, String.format("Customer %s cannot be null or empty", field));
    }
    
    public static InvalidCustomerDataException nullField(String field) {
        return new InvalidCustomerDataException(field, null, String.format("Customer %s cannot be null", field));
    }
    
    public static InvalidCustomerDataException emptyPatchRequest() {
        return new InvalidCustomerDataException("patchRequest", null, "At least one field must be provided for PATCH operation");
    }
    
    public static InvalidCustomerDataException nullSearchCriteria() {
        return new InvalidCustomerDataException("searchCriteria", null, "Search criteria cannot be null");
    }

    @Override
    public String getErrorCode() {
        return "INVALID_CUSTOMER_DATA";
    }

    @Override
    public Object getErrorContext() {
        return field != null ? new FieldContext(field, value) : null;
    }

    public record FieldContext(String field, Object value) {
    }
}
