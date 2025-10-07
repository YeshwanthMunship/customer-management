package com.example.customermanagement.domain.exception;

import java.time.format.DateTimeParseException;

public class InvalidDateFormatException extends DomainException {
    
    private final String dateString;
    private final String expectedFormat;
    
    public InvalidDateFormatException(String dateString, String expectedFormat) {
        super(String.format("Invalid date format: '%s'. Expected format: %s", dateString, expectedFormat));
        this.dateString = dateString;
        this.expectedFormat = expectedFormat;
    }
    
    public InvalidDateFormatException(String dateString, String expectedFormat, DateTimeParseException cause) {
        super(String.format("Invalid date format: '%s'. Expected format: %s", dateString, expectedFormat), cause);
        this.dateString = dateString;
        this.expectedFormat = expectedFormat;
    }
    
    public InvalidDateFormatException(String message) {
        super(message);
        this.dateString = null;
        this.expectedFormat = null;
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_DATE_FORMAT";
    }
    
    public String getDateString() {
        return dateString;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    @Override
    public Object getErrorContext() {
        return dateString != null ? new DateContext(dateString, expectedFormat) : null;
    }
    
    public record DateContext(String dateString, String expectedFormat) {
    }
    
    public static InvalidDateFormatException invalidDateTimeFormat(String dateTimeString, DateTimeParseException cause) {
        return new InvalidDateFormatException(dateTimeString, "yyyy-MM-dd'T'HH:mm:ss", cause);
    }
}