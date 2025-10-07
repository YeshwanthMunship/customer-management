package com.example.customermanagement.domain.exception;

public class AddressMappingException extends DomainException {
    
    private final Object sourceDto;
    private final Throwable cause;
    
    public AddressMappingException(String message, Object sourceDto, Throwable cause) {
        super(message, cause);
        this.sourceDto = sourceDto;
        this.cause = cause;
    }
    
    public AddressMappingException(String message, Object sourceDto) {
        super(message);
        this.sourceDto = sourceDto;
        this.cause = null;
    }
    
    @Override
    public String getErrorCode() {
        return "ADDRESS_MAPPING_ERROR";
    }
    
    public Object getSourceDto() {
        return sourceDto;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }
}