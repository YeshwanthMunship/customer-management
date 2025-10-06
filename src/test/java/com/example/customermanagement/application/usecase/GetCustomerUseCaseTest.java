package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
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
@DisplayName("Get Customer Use Case Tests")
class GetCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private GetCustomerUseCase getCustomerUseCase;

    private UUID customerId;
    private Customer sampleCustomer;
    private CustomerResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        sampleCustomer = new Customer(
            customerId,
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            new com.example.customermanagement.domain.model.Address(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
            )
        );
        sampleCustomer.setCreatedAt(now);
        sampleCustomer.setUpdatedAt(now);

        AddressDTO addressDTO = new AddressDTO(
            "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        sampleResponseDTO = new CustomerResponseDTO(
            customerId,
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            addressDTO,
            now,
            now
        );
    }

    @Nested
    @DisplayName("execute Tests")
    class ExecuteTests {

        @Test
        @DisplayName("Should return customer when found")
        void shouldReturnCustomerWhenFound() {
            // Given
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
            when(customerMapper.toResponseDto(sampleCustomer)).thenReturn(sampleResponseDTO);

            // When
            CustomerResponseDTO result = getCustomerUseCase.execute(customerId);

            // Then
            assertNotNull(result);
            assertEquals(sampleResponseDTO, result);
            assertEquals(customerId, result.getId());
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("rajesh.kumar@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            assertNotNull(result.getAddress());
            assertEquals("123 MG Road", result.getAddress().getStreet());
            assertEquals("Mumbai", result.getAddress().getCity());
            assertEquals("Maharashtra", result.getAddress().getState());
            assertEquals("400001", result.getAddress().getZipCode());
            assertEquals("India", result.getAddress().getCountry());

            verify(customerRepository).findById(customerId);
            verify(customerMapper).toResponseDto(sampleCustomer);
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFound() {
            // Given
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> getCustomerUseCase.execute(customerId)
            );

            assertEquals(customerId, exception.getCustomerId());
            assertTrue(exception.getMessage().contains(customerId.toString()));

            verify(customerRepository).findById(customerId);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should throw InvalidCustomerDataException when customer ID is null")
        void shouldThrowInvalidCustomerDataExceptionWhenCustomerIdIsNull() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> getCustomerUseCase.execute(null)
            );

            assertEquals("customerId", exception.getField());
            assertNull(exception.getValue());
            assertTrue(exception.getMessage().contains("Customer ID cannot be null"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should handle different customer IDs correctly")
        void shouldHandleDifferentCustomerIdsCorrectly() {
            // Given
            UUID differentCustomerId = UUID.randomUUID();
            Customer differentCustomer = new Customer(
                differentCustomerId,
                "Priya Sharma",
                "priya.sharma@example.com",
                "+91-8765432109",
                new com.example.customermanagement.domain.model.Address(
                    "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
                )
            );

            AddressDTO differentAddressDTO = new AddressDTO(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
            );
            CustomerResponseDTO differentResponseDTO = new CustomerResponseDTO(
                differentCustomerId,
                "Priya Sharma",
                "priya.sharma@example.com",
                "+91-8765432109",
                differentAddressDTO,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            when(customerRepository.findById(differentCustomerId)).thenReturn(Optional.of(differentCustomer));
            when(customerMapper.toResponseDto(differentCustomer)).thenReturn(differentResponseDTO);

            // When
            CustomerResponseDTO result = getCustomerUseCase.execute(differentCustomerId);

            // Then
            assertNotNull(result);
            assertEquals(differentResponseDTO, result);
            assertEquals(differentCustomerId, result.getId());
            assertEquals("Priya Sharma", result.getName());
            assertEquals("priya.sharma@example.com", result.getEmail());
            assertEquals("+91-8765432109", result.getPhone());

            verify(customerRepository).findById(differentCustomerId);
            verify(customerMapper).toResponseDto(differentCustomer);
        }

        @Test
        @DisplayName("Should handle customer with minimal data")
        void shouldHandleCustomerWithMinimalData() {
            // Given
            UUID minimalCustomerId = UUID.randomUUID();
            Customer minimalCustomer = new Customer(
                minimalCustomerId,
                "Minimal Customer",
                "minimal@example.com",
                "+91-1111111111",
                new com.example.customermanagement.domain.model.Address(
                    "Minimal Street", "Minimal City", "Minimal State", "111111", "India"
                )
            );

            AddressDTO minimalAddressDTO = new AddressDTO(
                "Minimal Street", "Minimal City", "Minimal State", "111111", "India"
            );
            CustomerResponseDTO minimalResponseDTO = new CustomerResponseDTO(
                minimalCustomerId,
                "Minimal Customer",
                "minimal@example.com",
                "+91-1111111111",
                minimalAddressDTO,
                null,
                null
            );

            when(customerRepository.findById(minimalCustomerId)).thenReturn(Optional.of(minimalCustomer));
            when(customerMapper.toResponseDto(minimalCustomer)).thenReturn(minimalResponseDTO);

            // When
            CustomerResponseDTO result = getCustomerUseCase.execute(minimalCustomerId);

            // Then
            assertNotNull(result);
            assertEquals(minimalResponseDTO, result);
            assertEquals(minimalCustomerId, result.getId());
            assertEquals("Minimal Customer", result.getName());
            assertEquals("minimal@example.com", result.getEmail());
            assertEquals("+91-1111111111", result.getPhone());

            verify(customerRepository).findById(minimalCustomerId);
            verify(customerMapper).toResponseDto(minimalCustomer);
        }
    }
}
