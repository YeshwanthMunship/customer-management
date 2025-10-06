package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Customer Use Case Tests")
class DeleteCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DeleteCustomerUseCase deleteCustomerUseCase;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("execute Tests")
    class ExecuteTests {

        @Test
        @DisplayName("Should delete customer when found")
        void shouldDeleteCustomerWhenFound() {
            // Given
            when(customerRepository.existsById(customerId)).thenReturn(true);

            // When
            assertDoesNotThrow(() -> deleteCustomerUseCase.execute(customerId));

            // Then
            verify(customerRepository).existsById(customerId);
            verify(customerRepository).deleteById(customerId);
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFound() {
            // Given
            when(customerRepository.existsById(customerId)).thenReturn(false);

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> deleteCustomerUseCase.execute(customerId)
            );

            assertEquals(customerId, exception.getCustomerId());
            assertTrue(exception.getMessage().contains(customerId.toString()));

            verify(customerRepository).existsById(customerId);
            verify(customerRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw InvalidCustomerDataException when customer ID is null")
        void shouldThrowInvalidCustomerDataExceptionWhenCustomerIdIsNull() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> deleteCustomerUseCase.execute(null)
            );

            assertEquals("customerId", exception.getField());
            assertNull(exception.getValue());
            assertTrue(exception.getMessage().contains("Customer ID cannot be null"));

            verifyNoInteractions(customerRepository);
        }

        @Test
        @DisplayName("Should handle different customer IDs correctly")
        void shouldHandleDifferentCustomerIdsCorrectly() {
            // Given
            UUID differentCustomerId = UUID.randomUUID();
            when(customerRepository.existsById(differentCustomerId)).thenReturn(true);

            // When
            assertDoesNotThrow(() -> deleteCustomerUseCase.execute(differentCustomerId));

            // Then
            verify(customerRepository).existsById(differentCustomerId);
            verify(customerRepository).deleteById(differentCustomerId);
        }

        @Test
        @DisplayName("Should not delete when customer does not exist")
        void shouldNotDeleteWhenCustomerDoesNotExist() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(customerRepository.existsById(nonExistentId)).thenReturn(false);

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> deleteCustomerUseCase.execute(nonExistentId)
            );

            assertEquals(nonExistentId, exception.getCustomerId());

            verify(customerRepository).existsById(nonExistentId);
            verify(customerRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {
            // Given
            when(customerRepository.existsById(customerId)).thenReturn(true);
            doThrow(new RuntimeException("Database error")).when(customerRepository).deleteById(customerId);

            // When & Then
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteCustomerUseCase.execute(customerId)
            );

            assertEquals("Database error", exception.getMessage());

            verify(customerRepository).existsById(customerId);
            verify(customerRepository).deleteById(customerId);
        }

        @Test
        @DisplayName("Should handle existsById exception gracefully")
        void shouldHandleExistsByIdExceptionGracefully() {
            // Given
            when(customerRepository.existsById(customerId)).thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteCustomerUseCase.execute(customerId)
            );

            assertEquals("Database connection error", exception.getMessage());

            verify(customerRepository).existsById(customerId);
            verify(customerRepository, never()).deleteById(any());
        }
    }
}