package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.AddressMapper;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.address.AddressDTO;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patch Customer Use Case Tests")
class PatchCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CustomerMapper customerMapper;
    
    @Mock
    private AddressMapper addressMapper;

    private PatchCustomerUseCase patchCustomerUseCase;
    
    private UUID customerId;
    private Customer existingCustomer;
    private Address existingAddress;

    @BeforeEach
    void setUp() {
        patchCustomerUseCase = new PatchCustomerUseCase(customerRepository, customerMapper, addressMapper);
        
        customerId = UUID.randomUUID();
        existingAddress = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        existingCustomer = new Customer(customerId, "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", existingAddress);
    }

    @Nested
    @DisplayName("Successful Patch Operations")
    class SuccessfulPatchOperations {

        @Test
        @DisplayName("Should patch customer name only")
        void shouldPatchCustomerNameOnly() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setName("Priya Sharma");
            
            Customer updatedCustomer = new Customer(customerId, "Priya Sharma", "rajesh.kumar@example.com", "+91-9876543210", existingAddress);
            CustomerResponseDTO expectedResponse = createResponseDTO(updatedCustomer);
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));
            when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

            // When
            CustomerResponseDTO result = patchCustomerUseCase.execute(customerId, patchDTO);

            // Then
            assertNotNull(result);
            assertEquals("Priya Sharma", result.getName());
            assertEquals("rajesh.kumar@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            verify(customerRepository).findById(customerId);
            verify(customerRepository).update(eq(customerId), any(Customer.class));
        }

        @Test
        @DisplayName("Should patch customer email only")
        void shouldPatchCustomerEmailOnly() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setEmail("priya.sharma@example.com");
            
            Customer updatedCustomer = new Customer(customerId, "Rajesh Kumar", "priya.sharma@example.com", "+91-9876543210", existingAddress);
            CustomerResponseDTO expectedResponse = createResponseDTO(updatedCustomer);
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));
            when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

            // When
            CustomerResponseDTO result = patchCustomerUseCase.execute(customerId, patchDTO);

            // Then
            assertNotNull(result);
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("priya.sharma@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
        }

        @Test
        @DisplayName("Should patch customer phone only")
        void shouldPatchCustomerPhoneOnly() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setPhone("+91-8765432109");
            
            Customer updatedCustomer = new Customer(customerId, "Rajesh Kumar", "rajesh.kumar@example.com", "+91-8765432109", existingAddress);
            CustomerResponseDTO expectedResponse = createResponseDTO(updatedCustomer);
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));
            when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

            // When
            CustomerResponseDTO result = patchCustomerUseCase.execute(customerId, patchDTO);

            // Then
            assertNotNull(result);
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("rajesh.kumar@example.com", result.getEmail());
            assertEquals("+91-8765432109", result.getPhone());
        }

        @Test
        @DisplayName("Should patch customer address only")
        void shouldPatchCustomerAddressOnly() {
            // Given
            AddressDTO newAddressDTO = new AddressDTO("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
            Address newAddress = new Address("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
            
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setAddress(newAddressDTO);
            
            Customer updatedCustomer = new Customer(customerId, "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", newAddress);
            CustomerResponseDTO expectedResponse = createResponseDTO(updatedCustomer);
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(addressMapper.toEntity(newAddressDTO)).thenReturn(newAddress);
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));
            when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

            // When
            CustomerResponseDTO result = patchCustomerUseCase.execute(customerId, patchDTO);

            // Then
            assertNotNull(result);
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("rajesh.kumar@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            verify(addressMapper).toEntity(newAddressDTO);
        }

        @Test
        @DisplayName("Should patch multiple fields")
        void shouldPatchMultipleFields() {
            // Given
            AddressDTO newAddressDTO = new AddressDTO("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
            Address newAddress = new Address("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
            
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setName("Priya Sharma");
            patchDTO.setEmail("priya.sharma@example.com");
            patchDTO.setPhone("+91-8765432109");
            patchDTO.setAddress(newAddressDTO);
            
            Customer updatedCustomer = new Customer(customerId, "Priya Sharma", "priya.sharma@example.com", "+91-8765432109", newAddress);
            CustomerResponseDTO expectedResponse = createResponseDTO(updatedCustomer);
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(addressMapper.toEntity(newAddressDTO)).thenReturn(newAddress);
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));
            when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

            // When
            CustomerResponseDTO result = patchCustomerUseCase.execute(customerId, patchDTO);

            // Then
            assertNotNull(result);
            assertEquals("Priya Sharma", result.getName());
            assertEquals("priya.sharma@example.com", result.getEmail());
            assertEquals("+91-8765432109", result.getPhone());
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should throw exception for null customer ID")
        void shouldThrowExceptionForNullCustomerId() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setName("Jane Doe");

            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> patchCustomerUseCase.execute(null, patchDTO)
            );
            
            assertEquals("Customer ID cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for null patch DTO")
        void shouldThrowExceptionForNullPatchDTO() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> patchCustomerUseCase.execute(customerId, null)
            );
            
            assertEquals("Customer cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty patch DTO")
        void shouldThrowExceptionForEmptyPatchDTO() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();

            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> patchCustomerUseCase.execute(customerId, patchDTO)
            );
            
            assertEquals("At least one field must be provided for PATCH operation", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setName("Priya Sharma");
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> patchCustomerUseCase.execute(customerId, patchDTO)
            );
            
            assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when update fails")
        void shouldThrowExceptionWhenUpdateFails() {
            // Given
            CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
            patchDTO.setName("Priya Sharma");
            
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.update(eq(customerId), any(Customer.class))).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> patchCustomerUseCase.execute(customerId, patchDTO)
            );
            
            assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        }
    }

    private CustomerResponseDTO createResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }
}
