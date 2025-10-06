package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    public UpdateCustomerUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerResponseDTO execute(UUID customerId, CustomerRequestDTO requestDTO) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        if (requestDTO == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }
        
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        Customer updatedCustomer = customerMapper.toEntity(requestDTO);
        updatedCustomer.setCreatedAt(existingCustomer.getCreatedAt());
        
        Customer savedCustomer = customerRepository.update(customerId, updatedCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        return customerMapper.toResponseDto(savedCustomer);
    }
    
}
