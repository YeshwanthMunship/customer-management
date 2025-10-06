package com.example.customermanagement.domain.validator;


import com.example.customermanagement.domain.exception.InvalidEmailFormatException;

public class EmailValidator {
    
    public static void validateEmail(String email) {
        InvalidEmailFormatException.EmailValidationError error = validateEmailFormat(email);
        if (error != null) {
            throw new InvalidEmailFormatException(email, error);
        }
    }
    
    private static InvalidEmailFormatException.EmailValidationError validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return InvalidEmailFormatException.EmailValidationError.GENERAL_FORMAT;
        }
        
        String trimmedEmail = email.trim();
        
        if (trimmedEmail.length() > 254) {
            return InvalidEmailFormatException.EmailValidationError.TOO_LONG;
        }
        
        long atCount = trimmedEmail.chars().filter(ch -> ch == '@').count();
        if (atCount == 0) {
            return InvalidEmailFormatException.EmailValidationError.MISSING_AT_SYMBOL;
        }
        if (atCount > 1) {
            return InvalidEmailFormatException.EmailValidationError.MULTIPLE_AT_SYMBOLS;
        }
        
        int atIndex = trimmedEmail.indexOf('@');
        String localPart = trimmedEmail.substring(0, atIndex);
        String domainPart = trimmedEmail.substring(atIndex + 1);
        
        InvalidEmailFormatException.EmailValidationError localError = validateLocalPart(localPart);
        if (localError != null) {
            return localError;
        }
        
        return validateDomainPart(domainPart);
    }
    
    private static InvalidEmailFormatException.EmailValidationError validateLocalPart(String localPart) {
        if (localPart == null || localPart.isEmpty()) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_LOCAL_PART;
        }
        
        if (localPart.length() > 64) {
            return InvalidEmailFormatException.EmailValidationError.LOCAL_PART_TOO_LONG;
        }
        
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT;
        }
        
        if (localPart.contains("..")) {
            return InvalidEmailFormatException.EmailValidationError.CONSECUTIVE_DOTS;
        }
        
        if (!localPart.matches("^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*$")) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_LOCAL_PART;
        }
        
        return null;
    }
    
    private static InvalidEmailFormatException.EmailValidationError validateDomainPart(String domainPart) {
        if (domainPart == null || domainPart.isEmpty()) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART;
        }
        
        if (domainPart.length() > 253) {
            return InvalidEmailFormatException.EmailValidationError.DOMAIN_TOO_LONG;
        }
        
        if (!domainPart.contains(".")) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART;
        }
        
        if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT;
        }
        
        if (domainPart.startsWith("-") || domainPart.endsWith("-")) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART;
        }
        
        String[] labels = domainPart.split("\\.");
        if (labels.length < 2) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART;
        }
        
        for (String label : labels) {
            if (!isValidDomainLabel(label)) {
                return InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART;
            }
        }
        
        String tld = labels[labels.length - 1];
        if (tld.length() < 2 || !tld.matches("^[a-zA-Z]+$")) {
            return InvalidEmailFormatException.EmailValidationError.INVALID_TLD;
        }
        
        return null;
    }
    
    private static boolean isValidDomainLabel(String label) {
        if (label == null || label.isEmpty() || label.length() > 63) {
            return false;
        }
        
        if (label.startsWith("-") || label.endsWith("-")) {
            return false;
        }
        
        return label.matches("^[a-zA-Z0-9-]+$");
    }
}
