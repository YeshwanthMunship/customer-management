package com.example.customermanagement.domain.exception;

public class InvalidDateFormatException extends DomainException {
    
    private final String invalidDate;
    private final String expectedFormat;
    
    public InvalidDateFormatException(String invalidDate, String expectedFormat, String message) {
        super(message);
        this.invalidDate = invalidDate;
        this.expectedFormat = expectedFormat;
    }
    
    public InvalidDateFormatException(String invalidDate, String expectedFormat, String message, Throwable cause) {
        super(message, cause);
        this.invalidDate = invalidDate;
        this.expectedFormat = expectedFormat;
    }
    
    public String getInvalidDate() {
        return invalidDate;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    public static InvalidDateFormatException invalidDateTimeFormat(String invalidDate) {
        return new InvalidDateFormatException(
            invalidDate, 
            "ISO 8601 format (e.g., '2023-10-15T10:30:00')",
            String.format("Invalid date format: '%s'. Expected ISO 8601 format (e.g., '2023-10-15T10:30:00')", invalidDate)
        );
    }
    
    public static InvalidDateFormatException invalidDateTimeFormat(String invalidDate, Throwable cause) {
        return new InvalidDateFormatException(
            invalidDate, 
            "ISO 8601 format (e.g., '2023-10-15T10:30:00')",
            String.format("Invalid date format: '%s'. Expected ISO 8601 format (e.g., '2023-10-15T10:30:00')", invalidDate),
            cause
        );
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_DATE_FORMAT";
    }
    
    @Override
    public Object getErrorContext() {
        return new DateFormatContext(invalidDate, expectedFormat);
    }
    
    public record DateFormatContext(String invalidDate, String expectedFormat) {
    }
}
