package com.example.customermanagement.infrastructure.persistence;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryCustomerRepository.
 * Tests the infrastructure implementation of the customer repository.
 */
class InMemoryCustomerRepositoryTest {

    private InMemoryCustomerRepository repository;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCustomerRepository();
        testAddress = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
    }

    @Test
    void shouldSaveCustomerSuccessfully() {
        // Given
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);

        // When
        Customer savedCustomer = repository.save(customer);

        // Then
        assertNotNull(savedCustomer);
        assertEquals(customer.getId(), savedCustomer.getId());
        assertEquals("Rajesh Kumar", savedCustomer.getName());
    }

    @Test
    void shouldThrowExceptionWhenSavingNullCustomer() {
        // When & Then
        assertThrows(InvalidCustomerDataException.class, () -> repository.save(null));
    }

    @Test
    void shouldFindCustomerById() {
        // Given
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        repository.save(customer);

        // When
        Optional<Customer> found = repository.findById(customer.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(customer.getId(), found.get().getId());
        assertEquals("Rajesh Kumar", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Customer> found = repository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyForNullId() {
        // When
        Optional<Customer> found = repository.findById(null);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAllCustomers() {
        // Given
        Customer customer1 = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        Customer customer2 = new Customer("Priya Sharma", "priya.sharma@example.com", "+91-8765432109", testAddress);
        
        repository.save(customer1);
        repository.save(customer2);

        // When
        List<Customer> customers = repository.findAll();

        // Then
        assertEquals(2, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Rajesh Kumar")));
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Priya Sharma")));
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        repository.save(customer);
        
        Address newAddress = new Address("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
        Customer updatedCustomer = new Customer("Rajesh Updated", "rajesh.updated@example.com", "+91-8765432109", newAddress);

        // When
        Optional<Customer> result = repository.update(customer.getId(), updatedCustomer);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getId());
        assertEquals("Rajesh Updated", result.get().getName());
        assertEquals("rajesh.updated@example.com", result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentCustomer() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);

        // When
        Optional<Customer> result = repository.update(nonExistentId, customer);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldDeleteCustomer() {
        // Given
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        repository.save(customer);

        // When
        boolean deleted = repository.deleteById(customer.getId());

        // Then
        assertTrue(deleted);
        assertFalse(repository.findById(customer.getId()).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentCustomer() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        boolean deleted = repository.deleteById(nonExistentId);

        // Then
        assertFalse(deleted);
    }

    @Test
    void shouldCheckIfCustomerExists() {
        // Given
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        repository.save(customer);

        // When & Then
        assertTrue(repository.existsById(customer.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
        assertFalse(repository.existsById(null));
    }

    @Test
    void shouldCountCustomers() {
        // Given
        Customer customer1 = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", testAddress);
        Customer customer2 = new Customer("Priya Sharma", "priya.sharma@example.com", "+91-8765432109", testAddress);
        
        // When & Then
        assertEquals(0, repository.count());
        
        repository.save(customer1);
        assertEquals(1, repository.count());
        
        repository.save(customer2);
        assertEquals(2, repository.count());
        
        repository.deleteById(customer1.getId());
        assertEquals(1, repository.count());
    }
}
