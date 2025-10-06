package com.example.customermanagement.domain.exception;

public class CustomerMappingException extends DomainException {
    
    private final Object sourceDto;
    private final Throwable cause;
    
    public CustomerMappingException(String message, Object sourceDto, Throwable cause) {
        super(message, cause);
        this.sourceDto = sourceDto;
        this.cause = cause;
    }
    
    public CustomerMappingException(String message, Object sourceDto) {
        super(message);
        this.sourceDto = sourceDto;
        this.cause = null;
    }
    
    @Override
    public String getErrorCode() {
        return "CUSTOMER_MAPPING_ERROR";
    }
    
    public Object getSourceDto() {
        return sourceDto;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }
}
