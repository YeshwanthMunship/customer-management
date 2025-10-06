package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllCustomersUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    public GetAllCustomersUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    public List<CustomerResponseDTO> execute() {
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return customerMapper.toResponseDtoList(customers);
    }

    public PageResponseDTO<CustomerResponseDTO> execute(int page, int size) {
        if (page < 0) {
            throw InvalidCustomerDataException.invalidPagination("page", page, "Page number cannot be negative");
        }
        if (size <= 0) {
            throw InvalidCustomerDataException.invalidPagination("size", size, "Page size must be greater than 0");
        }
        
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
        
        long totalCount = customerRepository.count();
        List<CustomerResponseDTO> customerDTOs = customerMapper.toResponseDtoList(customers);
        return new PageResponseDTO<>(customerDTOs, page, size, totalCount);
    }
}
