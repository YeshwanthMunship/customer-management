package com.example.customermanagement.domain.model;

import com.example.customermanagement.domain.exception.InvalidAddressException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Address value object.
 * Tests the immutability and validation of the Address value object.
 */
class AddressTest {

    @Test
    void shouldCreateAddressWithValidData() {
        // When
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // Then
        assertEquals("123 MG Road", address.getStreet());
        assertEquals("Mumbai", address.getCity());
        assertEquals("Maharashtra", address.getState());
        assertEquals("400001", address.getZipCode());
        assertEquals("India", address.getCountry());
    }

    @Test
    void shouldTrimWhitespaceFromFields() {
        // When
        Address address = new Address("  123 MG Road  ", "  Mumbai  ", "  Maharashtra  ", "  400001  ", "  India  ");
        
        // Then
        assertEquals("123 MG Road", address.getStreet());
        assertEquals("Mumbai", address.getCity());
        assertEquals("Maharashtra", address.getState());
        assertEquals("400001", address.getZipCode());
        assertEquals("India", address.getCountry());
    }

    @Test
    void shouldThrowExceptionForNullStreet() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address(null, "Springfield", "IL", "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForEmptyStreet() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("", "Springfield", "IL", "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForWhitespaceOnlyStreet() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("   ", "Springfield", "IL", "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForNullCity() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("123 Main St", null, "IL", "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForEmptyCity() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("123 Main St", "", "IL", "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForNullState() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("123 Main St", "Springfield", null, "62701", "USA"));
    }

    @Test
    void shouldThrowExceptionForNullZipCode() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("123 Main St", "Springfield", "IL", null, "USA"));
    }

    @Test
    void shouldThrowExceptionForNullCountry() {
        // When & Then
        assertThrows(InvalidAddressException.class, () -> 
            new Address("123 Main St", "Springfield", "IL", "62701", null));
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        // Given
        Address address1 = new Address("123 Main St", "Springfield", "IL", "62701", "USA");
        Address address2 = new Address("123 Main St", "Springfield", "IL", "62701", "USA");
        
        // When & Then
        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        // Given
        Address address1 = new Address("123 Main St", "Springfield", "IL", "62701", "USA");
        Address address2 = new Address("456 Oak Ave", "Springfield", "IL", "62701", "USA");
        
        // When & Then
        assertNotEquals(address1, address2);
    }

    @Test
    void shouldHaveConsistentToString() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When
        String toString = address.toString();
        
        // Then
        assertTrue(toString.contains("123 MG Road"));
        assertTrue(toString.contains("Mumbai"));
        assertTrue(toString.contains("Maharashtra"));
        assertTrue(toString.contains("400001"));
        assertTrue(toString.contains("India"));
    }
}
