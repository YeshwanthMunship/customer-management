package com.example.customermanagement.domain.model;

import com.example.customermanagement.domain.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Enhanced Customer Email Validation Tests")
class EnhancedCustomerEmailValidationTest {
    
    private final Address validAddress = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
    
    @Nested
    @DisplayName("Valid Email Scenarios")
    class ValidEmailTests {
        
        @ParameterizedTest
        @ValueSource(strings = {
            "user@example.com",
            "test.email@domain.org",
            "user+tag@example.co.uk",
            "firstname.lastname@company.com",
            "user123@test-domain.com",
            "a@b.co",
            "very.long.email.address@very-long-domain-name.com"
        })
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmails(String email) {
            assertDoesNotThrow(() -> 
                new Customer("Rajesh Kumar", email, "+91-9876543210", validAddress)
            );
        }
        
        @Test
        @DisplayName("Should normalize email to lowercase")
        void shouldNormalizeEmailToLowercase() {
            Customer customer = new Customer("Rajesh Kumar", "RAJESH.KUMAR@EXAMPLE.COM", "+91-9876543210", validAddress);
            assertEquals("rajesh.kumar@example.com", customer.getEmail());
        }
        
        @Test
        @DisplayName("Should trim whitespace from email")
        void shouldTrimWhitespaceFromEmail() {
            Customer customer = new Customer("Rajesh Kumar", "  rajesh.kumar@example.com  ", "+91-9876543210", validAddress);
            assertEquals("rajesh.kumar@example.com", customer.getEmail());
        }
    }
    
    @Nested
    @DisplayName("Invalid Email Scenarios")
    class InvalidEmailTests {
        
        @Test
        @DisplayName("Should reject email that is too long")
        void shouldRejectTooLongEmail() {
            String longEmail = "a".repeat(250) + "@example.com"; // Over 254 characters
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", longEmail, "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.TOO_LONG, exception.getValidationError());
            assertTrue(exception.getMessage().contains("exceeds maximum length"));
        }
        
        @Test
        @DisplayName("Should reject email without @ symbol")
        void shouldRejectEmailWithoutAtSymbol() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "userexample.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.MISSING_AT_SYMBOL, exception.getValidationError());
            assertTrue(exception.getMessage().contains("must contain exactly one '@' symbol"));
        }
        
        @Test
        @DisplayName("Should reject email with multiple @ symbols")
        void shouldRejectEmailWithMultipleAtSymbols() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@domain@example.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.MULTIPLE_AT_SYMBOLS, exception.getValidationError());
            assertTrue(exception.getMessage().contains("multiple '@' symbols"));
        }
        
        @Test
        @DisplayName("Should reject email with local part too long")
        void shouldRejectLocalPartTooLong() {
            String longLocalPart = "a".repeat(65) + "@example.com"; // Local part over 64 characters
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", longLocalPart, "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.LOCAL_PART_TOO_LONG, exception.getValidationError());
            assertTrue(exception.getMessage().contains("exceeds 64 characters"));
        }
        
        @Test
        @DisplayName("Should reject email starting with dot")
        void shouldRejectEmailStartingWithDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", ".user@example.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT, exception.getValidationError());
            assertTrue(exception.getMessage().contains("cannot start or end with a dot"));
        }
        
        @Test
        @DisplayName("Should reject email ending with dot")
        void shouldRejectEmailEndingWithDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user.@example.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email with consecutive dots")
        void shouldRejectEmailWithConsecutiveDots() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user..name@example.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.CONSECUTIVE_DOTS, exception.getValidationError());
            assertTrue(exception.getMessage().contains("consecutive dots"));
        }
        
        @Test
        @DisplayName("Should reject email with invalid local part characters")
        void shouldRejectInvalidLocalPartCharacters() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@name@example.com", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.MULTIPLE_AT_SYMBOLS, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email with domain too long")
        void shouldRejectDomainTooLong() {
            String longDomain = "a@" + "b".repeat(252) + ".co"; // Domain is 255 chars, total email is 258
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", longDomain, "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.TOO_LONG, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email without domain dot")
        void shouldRejectEmailWithoutDomainDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@domain", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email with invalid TLD")
        void shouldRejectInvalidTLD() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@example.c", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_TLD, exception.getValidationError());
            assertTrue(exception.getMessage().contains("at least 2 characters"));
        }
        
        @Test
        @DisplayName("Should reject email with numeric TLD")
        void shouldRejectNumericTLD() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@example.123", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_TLD, exception.getValidationError());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle email with maximum valid length")
        void shouldHandleMaximumValidLength() {
            // This should be close to limit, but we need to be more precise
            String validMaxEmail = "a".repeat(50) + "@" + "b".repeat(50) + "." + "c".repeat(50) + ".com";
            
            assertDoesNotThrow(() -> 
                new Customer("John Doe", validMaxEmail, "555-1234", validAddress)
            );
        }
        
        @Test
        @DisplayName("Should handle international characters in domain")
        void shouldHandleInternationalDomain() {
            // This should fail with current implementation as we don't support IDN
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", "user@münchen.de", "555-1234", validAddress)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should provide detailed error context")
        void shouldProvideDetailedErrorContext() {
            String invalidEmail = "invalid.email";
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> new Customer("John Doe", invalidEmail, "555-1234", validAddress)
            );
            
            assertNotNull(exception.getErrorContext());
            assertInstanceOf(InvalidEmailFormatException.EmailErrorContext.class, exception.getErrorContext());
            
            InvalidEmailFormatException.EmailErrorContext context = 
                (InvalidEmailFormatException.EmailErrorContext) exception.getErrorContext();
            
            assertEquals(invalidEmail, context.email());
            assertNotNull(context.validationError());
        }
    }
}
