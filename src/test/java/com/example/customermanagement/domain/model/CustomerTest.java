package com.example.customermanagement.domain.model;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Customer domain entity.
 * Tests the business logic and invariants of the Customer entity.
 */
class CustomerTest {

    @Test
    void shouldCreateCustomerWithValidData() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", address);
        
        // Then
        assertNotNull(customer.getId());
        assertEquals("Rajesh Kumar", customer.getName());
        assertEquals("rajesh.kumar@example.com", customer.getEmail());
        assertEquals("+91-9876543210", customer.getPhone());
        assertEquals(address, customer.getAddress());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
    }

    @Test
    void shouldCreateCustomerWithSpecificId() {
        // Given
        UUID customId = UUID.randomUUID();
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When
        Customer customer = new Customer(customId, "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", address);
        
        // Then
        assertEquals(customId, customer.getId());
        assertEquals("Rajesh Kumar", customer.getName());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When
        Customer customer = new Customer("Rajesh Kumar", "RAJESH.KUMAR@EXAMPLE.COM", "+91-9876543210", address);
        
        // Then
        assertEquals("rajesh.kumar@example.com", customer.getEmail());
    }

    @Test
    void shouldTrimWhitespaceFromFields() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When
        Customer customer = new Customer("  Rajesh Kumar  ", "  rajesh.kumar@example.com  ", "  +91-9876543210  ", address);
        
        // Then
        assertEquals("Rajesh Kumar", customer.getName());
        assertEquals("rajesh.kumar@example.com", customer.getEmail());
        assertEquals("+91-9876543210", customer.getPhone());
    }

    @Test
    void shouldThrowExceptionForNullName() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () -> 
            new Customer(null, "rajesh.kumar@example.com", "+91-9876543210", address));
    }

    @Test
    void shouldThrowExceptionForEmptyName() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () ->
            new Customer("", "rajesh.kumar@example.com", "+91-9876543210", address));
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () -> 
            new Customer("Rajesh Kumar", null, "+91-9876543210", address));
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When & Then
        assertThrows(InvalidEmailFormatException.class, () ->
            new Customer("Rajesh Kumar", "invalid-email", "+91-9876543210", address));
    }

    @Test
    void shouldThrowExceptionForNullPhone() {
        // Given
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () -> 
            new Customer("Rajesh Kumar", "rajesh.kumar@example.com", null, address));
    }

    @Test
    void shouldThrowExceptionForNullAddress() {
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () -> 
            new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", null));
    }

    @Test
    void shouldUpdateCustomerInfo() {
        // Given
        Address originalAddress = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", originalAddress);
        
        Address newAddress = new Address("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
        
        // When
        customer.updateInfo("Priya Sharma", "priya.sharma@example.com", "+91-8765432109", newAddress);
        
        // Then
        assertEquals("Priya Sharma", customer.getName());
        assertEquals("priya.sharma@example.com", customer.getEmail());
        assertEquals("+91-8765432109", customer.getPhone());
        assertEquals(newAddress, customer.getAddress());
    }
}
