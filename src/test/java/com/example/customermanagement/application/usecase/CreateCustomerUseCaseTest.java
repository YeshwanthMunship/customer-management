package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.address.AddressDTO;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CustomerMapper customerMapper;

    private CreateCustomerUseCase createCustomerUseCase;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = new CreateCustomerUseCase(customerRepository, customerMapper);
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        // Given
        AddressDTO addressDTO = new AddressDTO("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO);
        
        Address address = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        Customer customer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", address);
        Customer savedCustomer = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", address);
        
        CustomerResponseDTO expectedResponse = new CustomerResponseDTO();
        expectedResponse.setName("Rajesh Kumar");
        expectedResponse.setEmail("rajesh.kumar@example.com");
        
        when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toResponseDto(savedCustomer)).thenReturn(expectedResponse);

        // When
        CustomerResponseDTO result = createCustomerUseCase.execute(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Rajesh Kumar", result.getName());
        assertEquals("rajesh.kumar@example.com", result.getEmail());
        verify(customerMapper).toEntity(requestDTO);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponseDto(savedCustomer);
    }

    @Test
    void shouldThrowExceptionForNullCustomerRequestDTO() {
        // When & Then
        InvalidCustomerDataException exception = assertThrows(InvalidCustomerDataException.class, () -> 
            createCustomerUseCase.execute(null));
        
        assertEquals("Customer cannot be null", exception.getMessage());
        assertEquals("INVALID_CUSTOMER_DATA", exception.getErrorCode());
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toEntity(any());
        verify(customerMapper, never()).toResponseDto(any());
    }
}
