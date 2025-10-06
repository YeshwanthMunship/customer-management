package com.example.customermanagement.domain.validator;

import com.example.customermanagement.domain.exception.InvalidAddressException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Validator Tests")
class AddressValidatorTest {
    
    @Nested
    @DisplayName("Valid Address Data Tests")
    class ValidAddressDataTests {
        
        @Test
        @DisplayName("Should accept valid address data")
        void shouldAcceptValidAddressData() {
            assertDoesNotThrow(() -> 
                AddressValidator.validateAddressData("123 MG Road", "Mumbai", "Maharashtra", "400001", "India")
            );
        }
        
        @Test
        @DisplayName("Should sanitize address data correctly")
        void shouldSanitizeAddressDataCorrectly() {
            AddressValidator.AddressData sanitized = AddressValidator.normalizeAddressData(
                "  123 MG Road  ", "  Mumbai  ", "  Maharashtra  ", "  400001  ", "  India  "
            );
            
            assertEquals("123 MG Road", sanitized.street());
            assertEquals("Mumbai", sanitized.city());
            assertEquals("Maharashtra", sanitized.state());
            assertEquals("400001", sanitized.zipCode());
            assertEquals("India", sanitized.country());
        }
    }
    
    @Nested
    @DisplayName("Street Validation Tests")
    class StreetValidationTests {
        
        @Test
        @DisplayName("Should reject null street")
        void shouldRejectNullStreet() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateStreet(null)
            );
            
            assertEquals("street", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty street")
        void shouldRejectEmptyStreet() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateStreet("")
            );
            
            assertEquals("street", exception.getField());
        }
        
        @Test
        @DisplayName("Should reject whitespace-only street")
        void shouldRejectWhitespaceOnlyStreet() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateStreet("   ")
            );
            
            assertEquals("street", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid street")
        void shouldAcceptValidStreet() {
            assertDoesNotThrow(() -> AddressValidator.validateStreet("123 Main St"));
        }
    }
    
    @Nested
    @DisplayName("City Validation Tests")
    class CityValidationTests {
        
        @Test
        @DisplayName("Should reject null city")
        void shouldRejectNullCity() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateCity(null)
            );
            
            assertEquals("city", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty city")
        void shouldRejectEmptyCity() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateCity("")
            );
            
            assertEquals("city", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid city")
        void shouldAcceptValidCity() {
            assertDoesNotThrow(() -> AddressValidator.validateCity("Springfield"));
        }
    }
    
    @Nested
    @DisplayName("State Validation Tests")
    class StateValidationTests {
        
        @Test
        @DisplayName("Should reject null state")
        void shouldRejectNullState() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateState(null)
            );
            
            assertEquals("state", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty state")
        void shouldRejectEmptyState() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateState("")
            );
            
            assertEquals("state", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid state")
        void shouldAcceptValidState() {
            assertDoesNotThrow(() -> AddressValidator.validateState("IL"));
        }
    }
    
    @Nested
    @DisplayName("Zip Code Validation Tests")
    class ZipCodeValidationTests {
        
        @Test
        @DisplayName("Should reject null zip code")
        void shouldRejectNullZipCode() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateZipCode(null)
            );
            
            assertEquals("zipCode", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty zip code")
        void shouldRejectEmptyZipCode() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateZipCode("")
            );
            
            assertEquals("zipCode", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid zip code")
        void shouldAcceptValidZipCode() {
            assertDoesNotThrow(() -> AddressValidator.validateZipCode("62701"));
        }
    }
    
    @Nested
    @DisplayName("Country Validation Tests")
    class CountryValidationTests {
        
        @Test
        @DisplayName("Should reject null country")
        void shouldRejectNullCountry() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateCountry(null)
            );
            
            assertEquals("country", exception.getField());
            assertTrue(exception.getMessage().contains("cannot be null or empty"));
        }
        
        @Test
        @DisplayName("Should reject empty country")
        void shouldRejectEmptyCountry() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateCountry("")
            );
            
            assertEquals("country", exception.getField());
        }
        
        @Test
        @DisplayName("Should accept valid country")
        void shouldAcceptValidCountry() {
            assertDoesNotThrow(() -> AddressValidator.validateCountry("USA"));
        }
    }
    
    @Nested
    @DisplayName("Complete Address Data Validation Tests")
    class CompleteAddressDataValidationTests {
        
        @Test
        @DisplayName("Should validate all fields together")
        void shouldValidateAllFieldsTogether() {
            assertDoesNotThrow(() -> 
                AddressValidator.validateAddressData("123 MG Road", "Mumbai", "Maharashtra", "400001", "India")
            );
        }
        
        @Test
        @DisplayName("Should fail on first invalid field")
        void shouldFailOnFirstInvalidField() {
            // Should fail on street validation first
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateAddressData(null, "Springfield", "IL", "62701", "USA")
            );
            
            assertEquals("street", exception.getField());
        }
        
        @Test
        @DisplayName("Should fail on city when street is valid")
        void shouldFailOnCityWhenStreetIsValid() {
            InvalidAddressException exception = assertThrows(
                InvalidAddressException.class,
                () -> AddressValidator.validateAddressData("123 Main St", null, "IL", "62701", "USA")
            );
            
            assertEquals("city", exception.getField());
        }
    }
    
    @Nested
    @DisplayName("Data Sanitization Tests")
    class DataSanitizationTests {
        
        @Test
        @DisplayName("Should handle null values in sanitization")
        void shouldHandleNullValuesInSanitization() {
            AddressValidator.AddressData sanitized = AddressValidator.normalizeAddressData(null, null, null, null, null);
            
            assertNull(sanitized.street());
            assertNull(sanitized.city());
            assertNull(sanitized.state());
            assertNull(sanitized.zipCode());
            assertNull(sanitized.country());
        }
        
        @Test
        @DisplayName("Should trim whitespace from all fields")
        void shouldTrimWhitespaceFromAllFields() {
            AddressValidator.AddressData sanitized = AddressValidator.normalizeAddressData(
                "  123 Main St  ", "  Springfield  ", "  IL  ", "  62701  ", "  USA  "
            );
            
            assertEquals("123 Main St", sanitized.street());
            assertEquals("Springfield", sanitized.city());
            assertEquals("IL", sanitized.state());
            assertEquals("62701", sanitized.zipCode());
            assertEquals("USA", sanitized.country());
        }
        
        @Test
        @DisplayName("Should handle mixed null and valid values")
        void shouldHandleMixedNullAndValidValues() {
            AddressValidator.AddressData sanitized = AddressValidator.normalizeAddressData(
                "123 Main St", null, "  IL  ", "", "USA"
            );
            
            assertEquals("123 Main St", sanitized.street());
            assertNull(sanitized.city());
            assertEquals("IL", sanitized.state());
            assertEquals("", sanitized.zipCode());
            assertEquals("USA", sanitized.country());
        }
    }
}
