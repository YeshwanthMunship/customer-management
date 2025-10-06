package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import com.example.customermanagement.web.dto.address.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Customer Use Case Tests")
class UpdateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private UpdateCustomerUseCase updateCustomerUseCase;

    private UUID customerId;
    private Customer existingCustomer;
    private CustomerRequestDTO requestDTO;
    private Customer updatedCustomer;
    private Customer savedCustomer;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Existing customer
        existingCustomer = new Customer(
            customerId,
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            new com.example.customermanagement.domain.model.Address(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
            )
        );
        existingCustomer.setCreatedAt(now.minusDays(1));
        existingCustomer.setUpdatedAt(now.minusDays(1));

        // Request DTO
        AddressDTO addressDTO = new AddressDTO(
            "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
        );
        requestDTO = new CustomerRequestDTO(
            "Rajesh Kumar Updated",
            "rajesh.updated@example.com",
            "+91-8765432109",
            addressDTO
        );

        // Updated customer entity
        updatedCustomer = new Customer(
            customerId,
            "Rajesh Kumar Updated",
            "rajesh.updated@example.com",
            "+91-8765432109",
            new com.example.customermanagement.domain.model.Address(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
            )
        );
        updatedCustomer.setCreatedAt(existingCustomer.getCreatedAt()); // Preserve creation time

        // Saved customer (after repository update)
        savedCustomer = new Customer(
            customerId,
            "Rajesh Kumar Updated",
            "rajesh.updated@example.com",
            "+91-8765432109",
            new com.example.customermanagement.domain.model.Address(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
            )
        );
        savedCustomer.setCreatedAt(existingCustomer.getCreatedAt());
        savedCustomer.setUpdatedAt(now);

        // Response DTO
        responseDTO = new CustomerResponseDTO(
            customerId,
            "Rajesh Kumar Updated",
            "rajesh.updated@example.com",
            "+91-8765432109",
            addressDTO,
            existingCustomer.getCreatedAt(),
            now
        );
    }

    @Nested
    @DisplayName("execute Tests")
    class ExecuteTests {

        @Test
        @DisplayName("Should update customer successfully")
        void shouldUpdateCustomerSuccessfully() {
            // Given
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerMapper.toEntity(requestDTO)).thenReturn(updatedCustomer);
            when(customerRepository.update(customerId, updatedCustomer)).thenReturn(Optional.of(savedCustomer));
            when(customerMapper.toResponseDto(savedCustomer)).thenReturn(responseDTO);

            // When
            CustomerResponseDTO result = updateCustomerUseCase.execute(customerId, requestDTO);

            // Then
            assertNotNull(result);
            assertEquals(responseDTO, result);
            assertEquals(customerId, result.getId());
            assertEquals("Rajesh Kumar Updated", result.getName());
            assertEquals("rajesh.updated@example.com", result.getEmail());
            assertEquals("+91-8765432109", result.getPhone());
            assertNotNull(result.getAddress());
            assertEquals("456 Brigade Road", result.getAddress().getStreet());
            assertEquals("Bangalore", result.getAddress().getCity());
            assertEquals("Karnataka", result.getAddress().getState());
            assertEquals("560001", result.getAddress().getZipCode());
            assertEquals("India", result.getAddress().getCountry());

            verify(customerRepository).findById(customerId);
            verify(customerMapper).toEntity(requestDTO);
            verify(customerRepository).update(customerId, updatedCustomer);
            verify(customerMapper).toResponseDto(savedCustomer);
        }

        @Test
        @DisplayName("Should preserve creation timestamp when updating")
        void shouldPreserveCreationTimestampWhenUpdating() {
            // Given
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(5);
            existingCustomer.setCreatedAt(originalCreatedAt);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerMapper.toEntity(requestDTO)).thenReturn(updatedCustomer);
            when(customerRepository.update(customerId, updatedCustomer)).thenReturn(Optional.of(savedCustomer));
            when(customerMapper.toResponseDto(savedCustomer)).thenReturn(responseDTO);

            // When
            CustomerResponseDTO result = updateCustomerUseCase.execute(customerId, requestDTO);

            // Then
            assertNotNull(result);
            verify(customerMapper).toEntity(requestDTO);
            verify(customerRepository).update(eq(customerId), argThat(customer -> 
                customer.getCreatedAt().equals(originalCreatedAt)
            ));
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found during find")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFoundDuringFind() {
            // Given
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> updateCustomerUseCase.execute(customerId, requestDTO)
            );

            assertEquals(customerId, exception.getCustomerId());
            assertTrue(exception.getMessage().contains(customerId.toString()));

            verify(customerRepository).findById(customerId);
            verifyNoInteractions(customerMapper);
            verify(customerRepository, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found during update")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFoundDuringUpdate() {
            // Given
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerMapper.toEntity(requestDTO)).thenReturn(updatedCustomer);
            when(customerRepository.update(customerId, updatedCustomer)).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> updateCustomerUseCase.execute(customerId, requestDTO)
            );

            assertEquals(customerId, exception.getCustomerId());
            assertTrue(exception.getMessage().contains(customerId.toString()));

            verify(customerRepository).findById(customerId);
            verify(customerMapper).toEntity(requestDTO);
            verify(customerRepository).update(customerId, updatedCustomer);
            verify(customerMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("Should throw InvalidCustomerDataException when customer ID is null")
        void shouldThrowInvalidCustomerDataExceptionWhenCustomerIdIsNull() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> updateCustomerUseCase.execute(null, requestDTO)
            );

            assertEquals("customerId", exception.getField());
            assertNull(exception.getValue());
            assertTrue(exception.getMessage().contains("Customer ID cannot be null"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should throw InvalidCustomerDataException when request DTO is null")
        void shouldThrowInvalidCustomerDataExceptionWhenRequestDTOIsNull() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> updateCustomerUseCase.execute(customerId, null)
            );

            assertEquals("customer", exception.getField());
            assertNull(exception.getValue());
            assertTrue(exception.getMessage().contains("Customer cannot be null"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should handle partial updates correctly")
        void shouldHandlePartialUpdatesCorrectly() {
            // Given - Update only name and email
            CustomerRequestDTO partialRequestDTO = new CustomerRequestDTO();
            partialRequestDTO.setName("Rajesh Kumar Modified");
            partialRequestDTO.setEmail("rajesh.modified@example.com");
            partialRequestDTO.setPhone("+91-9876543210"); // Keep same phone
            partialRequestDTO.setAddress(new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India" // Keep same address
            ));

            Customer partialUpdatedCustomer = new Customer(
                customerId,
                "Rajesh Kumar Modified",
                "rajesh.modified@example.com",
                "+91-9876543210",
                new com.example.customermanagement.domain.model.Address(
                    "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
                )
            );
            partialUpdatedCustomer.setCreatedAt(existingCustomer.getCreatedAt());

            Customer partialSavedCustomer = new Customer(
                customerId,
                "Rajesh Kumar Modified",
                "rajesh.modified@example.com",
                "+91-9876543210",
                new com.example.customermanagement.domain.model.Address(
                    "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
                )
            );
            partialSavedCustomer.setCreatedAt(existingCustomer.getCreatedAt());
            partialSavedCustomer.setUpdatedAt(LocalDateTime.now());

            CustomerResponseDTO partialResponseDTO = new CustomerResponseDTO(
                customerId,
                "Rajesh Kumar Modified",
                "rajesh.modified@example.com",
                "+91-9876543210",
                new AddressDTO("123 MG Road", "Mumbai", "Maharashtra", "400001", "India"),
                existingCustomer.getCreatedAt(),
                LocalDateTime.now()
            );

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerMapper.toEntity(partialRequestDTO)).thenReturn(partialUpdatedCustomer);
            when(customerRepository.update(customerId, partialUpdatedCustomer)).thenReturn(Optional.of(partialSavedCustomer));
            when(customerMapper.toResponseDto(partialSavedCustomer)).thenReturn(partialResponseDTO);

            // When
            CustomerResponseDTO result = updateCustomerUseCase.execute(customerId, partialRequestDTO);

            // Then
            assertNotNull(result);
            assertEquals("Rajesh Kumar Modified", result.getName());
            assertEquals("rajesh.modified@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            assertEquals("123 MG Road", result.getAddress().getStreet());
            assertEquals("Mumbai", result.getAddress().getCity());

            verify(customerRepository).findById(customerId);
            verify(customerMapper).toEntity(partialRequestDTO);
            verify(customerRepository).update(customerId, partialUpdatedCustomer);
            verify(customerMapper).toResponseDto(partialSavedCustomer);
        }

        @Test
        @DisplayName("Should handle different customer IDs correctly")
        void shouldHandleDifferentCustomerIdsCorrectly() {
            // Given
            UUID differentCustomerId = UUID.randomUUID();
            Customer differentExistingCustomer = new Customer(
                differentCustomerId,
                "Different Customer",
                "different@example.com",
                "+91-1111111111",
                new com.example.customermanagement.domain.model.Address(
                    "Different Street", "Different City", "Different State", "111111", "India"
                )
            );

            Customer differentUpdatedCustomer = getCustomer(differentCustomerId, differentExistingCustomer);

            Customer differentSavedCustomer = getCustomer(differentCustomerId, differentExistingCustomer);
            differentSavedCustomer.setUpdatedAt(LocalDateTime.now());

            CustomerResponseDTO differentResponseDTO = new CustomerResponseDTO(
                differentCustomerId,
                "Different Customer Updated",
                "different.updated@example.com",
                "+91-2222222222",
                new AddressDTO("Updated Street", "Updated City", "Updated State", "222222", "India"),
                differentExistingCustomer.getCreatedAt(),
                LocalDateTime.now()
            );

            when(customerRepository.findById(differentCustomerId)).thenReturn(Optional.of(differentExistingCustomer));
            when(customerMapper.toEntity(requestDTO)).thenReturn(differentUpdatedCustomer);
            when(customerRepository.update(differentCustomerId, differentUpdatedCustomer)).thenReturn(Optional.of(differentSavedCustomer));
            when(customerMapper.toResponseDto(differentSavedCustomer)).thenReturn(differentResponseDTO);

            // When
            CustomerResponseDTO result = updateCustomerUseCase.execute(differentCustomerId, requestDTO);

            // Then
            assertNotNull(result);
            assertEquals(differentCustomerId, result.getId());
            assertEquals("Different Customer Updated", result.getName());

            verify(customerRepository).findById(differentCustomerId);
            verify(customerMapper).toEntity(requestDTO);
            verify(customerRepository).update(differentCustomerId, differentUpdatedCustomer);
            verify(customerMapper).toResponseDto(differentSavedCustomer);
        }
    }

    private static Customer getCustomer(UUID differentCustomerId, Customer differentExistingCustomer) {
        Customer differentUpdatedCustomer = new Customer(
                differentCustomerId,
            "Different Customer Updated",
            "different.updated@example.com",
            "+91-2222222222",
            new com.example.customermanagement.domain.model.Address(
                "Updated Street", "Updated City", "Updated State", "222222", "India"
            )
        );
        differentUpdatedCustomer.setCreatedAt(differentExistingCustomer.getCreatedAt());
        return differentUpdatedCustomer;
    }
}
