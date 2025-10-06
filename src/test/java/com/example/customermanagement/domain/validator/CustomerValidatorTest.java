package com.example.customermanagement.domain.validator;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.exception.InvalidEmailFormatException;
import com.example.customermanagement.domain.model.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Validator Tests")
class CustomerValidatorTest {
    
    private final Address validAddress = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
    
    @Nested
    @DisplayName("Valid Customer Data Tests")
    class ValidCustomerDataTests {
        
        @Test
        @DisplayName("Should accept valid customer data")
        void shouldAcceptValidCustomerData() {
            assertDoesNotThrow(() -> 
                CustomerValidator.validateCustomerData("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", validAddress)
            );
        }
        
        @Test
        @DisplayName("Should sanitize customer data correctly")
        void shouldSanitizeCustomerDataCorrectly() {
            CustomerValidator.CustomerData sanitized = CustomerValidator.normalizeCustomerData(
                "  Rajesh Kumar  ", "  RAJESH.KUMAR@EXAMPLE.COM  ", "  +91-9876543210  "
            );
            
            assertEquals("Rajesh Kumar", sanitized.name());
            assertEquals("rajesh.kumar@example.com", sanitized.email());
            assertEquals("+91-9876543210", sanitized.phone());
        }
    }
    
    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {
        
        @Test
        @DisplayName("Should reject null name")
        void shouldRejectNullName() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateName(null)
            );
            
            assertEquals("name", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty name")
        void shouldRejectEmptyName() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateName("")
            );
            
            assertEquals("name", exception.getField());
        }
        
        @Test
        @DisplayName("Should reject whitespace-only name")
        void shouldRejectWhitespaceOnlyName() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateName("   ")
            );
            
            assertEquals("name", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid name")
        void shouldAcceptValidName() {
            assertDoesNotThrow(() -> CustomerValidator.validateName("John Doe"));
        }
    }
    
    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {
        
        @Test
        @DisplayName("Should reject null email")
        void shouldRejectNullEmail() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateEmail(null)
            );
            
            assertEquals("email", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty email")
        void shouldRejectEmptyEmail() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateEmail("")
            );
            
            assertEquals("email", exception.getField());
        }
        
        @Test
        @DisplayName("Should reject invalid email format")
        void shouldRejectInvalidEmailFormat() {
            assertThrows(
                InvalidEmailFormatException.class,
                () -> CustomerValidator.validateEmail("invalid-email")
            );
        }
        
        @Test
        @DisplayName("Should accept valid email")
        void shouldAcceptValidEmail() {
            assertDoesNotThrow(() -> CustomerValidator.validateEmail("john.doe@example.com"));
        }
    }
    
    @Nested
    @DisplayName("Phone Validation Tests")
    class PhoneValidationTests {
        
        @Test
        @DisplayName("Should reject null phone")
        void shouldRejectNullPhone() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validatePhone(null)
            );
            
            assertEquals("phone", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty phone")
        void shouldRejectEmptyPhone() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validatePhone("")
            );
            
            assertEquals("phone", exception.getField());
        }
        
        @Test
        @DisplayName("Should reject whitespace-only phone")
        void shouldRejectWhitespaceOnlyPhone() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validatePhone("   ")
            );
            
            assertEquals("phone", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid phone")
        void shouldAcceptValidPhone() {
            assertDoesNotThrow(() -> CustomerValidator.validatePhone("555-1234"));
        }
    }
    
    @Nested
    @DisplayName("Address Validation Tests")
    class AddressValidationTests {
        
        @Test
        @DisplayName("Should reject null address")
        void shouldRejectNullAddress() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateAddress(null)
            );
            
            assertEquals("address", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null"));
        }
        
        @Test
        @DisplayName("Should accept valid address")
        void shouldAcceptValidAddress() {
            assertDoesNotThrow(() -> CustomerValidator.validateAddress(validAddress));
        }
    }
    
    @Nested
    @DisplayName("Complete Customer Data Validation Tests")
    class CompleteCustomerDataValidationTests {
        
        @Test
        @DisplayName("Should validate all fields together")
        void shouldValidateAllFieldsTogether() {
            assertDoesNotThrow(() -> 
                CustomerValidator.validateCustomerData("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", validAddress)
            );
        }
        
        @Test
        @DisplayName("Should fail on first invalid field")
        void shouldFailOnFirstInvalidField() {
            // Should fail on name validation first
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> CustomerValidator.validateCustomerData(null, "john.doe@example.com", "555-1234", validAddress)
            );
            
            assertEquals("name", exception.getField());
        }
    }
    
    @Nested
    @DisplayName("Data Sanitization Tests")
    class DataSanitizationTests {
        
        @Test
        @DisplayName("Should handle null values in sanitization")
        void shouldHandleNullValuesInSanitization() {
            CustomerValidator.CustomerData sanitized = CustomerValidator.normalizeCustomerData(null, null, null);
            
            assertNull(sanitized.name());
            assertNull(sanitized.email());
            assertNull(sanitized.phone());
        }
        
        @Test
        @DisplayName("Should trim whitespace from all fields")
        void shouldTrimWhitespaceFromAllFields() {
            CustomerValidator.CustomerData sanitized = CustomerValidator.normalizeCustomerData(
                "  John Doe  ", "  john.doe@example.com  ", "  555-1234  "
            );
            
            assertEquals("John Doe", sanitized.name());
            assertEquals("john.doe@example.com", sanitized.email());
            assertEquals("555-1234", sanitized.phone());
        }
        
        @Test
        @DisplayName("Should convert email to lowercase")
        void shouldConvertEmailToLowercase() {
            CustomerValidator.CustomerData sanitized = CustomerValidator.normalizeCustomerData(
                "John Doe", "JOHN.DOE@EXAMPLE.COM", "555-1234"
            );
            
            assertEquals("john.doe@example.com", sanitized.email());
        }
    }
}
