package com.example.customermanagement.domain.validator;

import com.example.customermanagement.domain.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Validator Tests")
class EmailValidatorTest {
    
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
            assertDoesNotThrow(() -> EmailValidator.validateEmail(email));
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
                () -> EmailValidator.validateEmail(longEmail)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.TOO_LONG, exception.getValidationError());
            assertTrue(exception.getMessage().contains("exceeds maximum length"));
        }
        
        @Test
        @DisplayName("Should reject email without @ symbol")
        void shouldRejectEmailWithoutAtSymbol() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("userexample.com")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.MISSING_AT_SYMBOL, exception.getValidationError());
            assertTrue(exception.getMessage().contains("must contain exactly one '@' symbol"));
        }
        
        @Test
        @DisplayName("Should reject email with multiple @ symbols")
        void shouldRejectEmailWithMultipleAtSymbols() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user@domain@example.com")
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
                () -> EmailValidator.validateEmail(longLocalPart)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.LOCAL_PART_TOO_LONG, exception.getValidationError());
            assertTrue(exception.getMessage().contains("exceeds 64 characters"));
        }
        
        @Test
        @DisplayName("Should reject email starting with dot")
        void shouldRejectEmailStartingWithDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail(".user@example.com")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT, exception.getValidationError());
            assertTrue(exception.getMessage().contains("cannot start or end with a dot"));
        }
        
        @Test
        @DisplayName("Should reject email ending with dot")
        void shouldRejectEmailEndingWithDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user.@example.com")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.STARTS_OR_ENDS_WITH_DOT, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email with consecutive dots")
        void shouldRejectEmailWithConsecutiveDots() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user..name@example.com")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.CONSECUTIVE_DOTS, exception.getValidationError());
            assertTrue(exception.getMessage().contains("consecutive dots"));
        }
        
        @Test
        @DisplayName("Should reject email with domain too long")
        void shouldRejectDomainTooLong() {
            String longDomain = "a@" + "b".repeat(252) + ".co"; // Domain is 255 chars (252 + 3), total email is 258, but we want to catch domain length first
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail(longDomain)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.TOO_LONG, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email without domain dot")
        void shouldRejectEmailWithoutDomainDot() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user@domain")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_DOMAIN_PART, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should reject email with invalid TLD")
        void shouldRejectInvalidTLD() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user@example.c")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_TLD, exception.getValidationError());
            assertTrue(exception.getMessage().contains("at least 2 characters"));
        }
        
        @Test
        @DisplayName("Should reject email with numeric TLD")
        void shouldRejectNumericTLD() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("user@example.123")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.INVALID_TLD, exception.getValidationError());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle null email")
        void shouldHandleNullEmail() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail(null)
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.GENERAL_FORMAT, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should handle empty email")
        void shouldHandleEmptyEmail() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.GENERAL_FORMAT, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should handle whitespace-only email")
        void shouldHandleWhitespaceOnlyEmail() {
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail("   ")
            );
            
            assertEquals(InvalidEmailFormatException.EmailValidationError.GENERAL_FORMAT, exception.getValidationError());
        }
        
        @Test
        @DisplayName("Should provide detailed error context")
        void shouldProvideDetailedErrorContext() {
            String invalidEmail = "invalid.email";
            
            InvalidEmailFormatException exception = assertThrows(
                InvalidEmailFormatException.class,
                () -> EmailValidator.validateEmail(invalidEmail)
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
