package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    public CreateCustomerUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    public CustomerResponseDTO execute(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }

        Customer customer = customerMapper.toEntity(requestDTO);
        Customer createdCustomer = customerRepository.save(customer);
                return customerMapper.toResponseDto(createdCustomer);
    }
}
