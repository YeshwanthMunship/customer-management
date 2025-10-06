package com.example.customermanagement.domain.exception;

public class InvalidEmailFormatException extends DomainException {
    
    private final String email;
    private final EmailValidationError validationError;
    
    public InvalidEmailFormatException(String email) {
        super(generateDetailedMessage(email, EmailValidationError.GENERAL_FORMAT));
        this.email = email;
        this.validationError = EmailValidationError.GENERAL_FORMAT;
    }
    
    public InvalidEmailFormatException(String email, EmailValidationError validationError) {
        super(generateDetailedMessage(email, validationError));
        this.email = email;
        this.validationError = validationError;
    }
    
    public InvalidEmailFormatException(String email, String customMessage) {
        super(customMessage);
        this.email = email;
        this.validationError = EmailValidationError.CUSTOM;
    }
    
    private static String generateDetailedMessage(String email, EmailValidationError error) {
        String baseMessage = "Invalid email format: '" + email + "'. ";
        
        return switch (error) {
            case TOO_LONG -> baseMessage + "Email address exceeds maximum length of 254 characters. Please use a shorter email address.";
            case MISSING_AT_SYMBOL -> baseMessage + "Email must contain exactly one '@' symbol. Example: user@domain.com";
            case MULTIPLE_AT_SYMBOLS -> baseMessage + "Email contains multiple '@' symbols. Only one '@' is allowed.";
            case INVALID_LOCAL_PART -> baseMessage + "The part before '@' contains invalid characters or format. Use only letters, numbers, and allowed special characters (!#$%&'*+/=?^_`{|}~-).";
            case INVALID_DOMAIN_PART -> baseMessage + "The domain part (after '@') is invalid. Domain must contain at least one dot and valid characters.";
            case LOCAL_PART_TOO_LONG -> baseMessage + "The part before '@' exceeds 64 characters. Please use a shorter local part.";
            case DOMAIN_TOO_LONG -> baseMessage + "The domain part exceeds 253 characters. Please use a shorter domain.";
            case CONSECUTIVE_DOTS -> baseMessage + "Email contains consecutive dots (..) which are not allowed.";
            case STARTS_OR_ENDS_WITH_DOT -> baseMessage + "Email cannot start or end with a dot (.).";
            case INVALID_TLD -> baseMessage + "Top-level domain (TLD) must be at least 2 characters and contain only letters.";
            case GENERAL_FORMAT -> baseMessage + "Email must follow the format: user@domain.com with valid characters and structure.";
            case CUSTOM -> baseMessage;
        };
    }
    
    @Override
    public String getErrorCode() {
        return "INVALID_EMAIL_FORMAT";
    }
    
    public String getEmail() {
        return email;
    }
    
    public EmailValidationError getValidationError() {
        return validationError;
    }
    
    @Override
    public Object getErrorContext() {
        return new EmailErrorContext(email, validationError);
    }
    
    public enum EmailValidationError {
        TOO_LONG,
        MISSING_AT_SYMBOL,
        MULTIPLE_AT_SYMBOLS,
        INVALID_LOCAL_PART,
        INVALID_DOMAIN_PART,
        LOCAL_PART_TOO_LONG,
        DOMAIN_TOO_LONG,
        CONSECUTIVE_DOTS,
        STARTS_OR_ENDS_WITH_DOT,
        INVALID_TLD,
        GENERAL_FORMAT,
        CUSTOM
    }
    
    public record EmailErrorContext(String email, EmailValidationError validationError) {
    }
}
