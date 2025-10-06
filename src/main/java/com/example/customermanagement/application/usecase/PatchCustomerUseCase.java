package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.AddressMapper;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatchCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    
    public PatchCustomerUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper, AddressMapper addressMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
    }

    public CustomerResponseDTO execute(UUID customerId, CustomerPatchRequestDTO patchDTO) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        if (patchDTO == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }
        if (!patchDTO.hasAnyField()) {
            throw InvalidCustomerDataException.emptyPatchRequest();
        }
        
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        String updatedName = patchDTO.hasName() ? patchDTO.getName() : existingCustomer.getName();
        String updatedEmail = patchDTO.hasEmail() ? patchDTO.getEmail() : existingCustomer.getEmail();
        String updatedPhone = patchDTO.hasPhone() ? patchDTO.getPhone() : existingCustomer.getPhone();
        
        var updatedAddress = patchDTO.hasAddress() 
            ? addressMapper.toEntity(patchDTO.getAddress())
            : existingCustomer.getAddress();
        
        Customer updatedCustomer = new Customer(
            existingCustomer.getId(),
            updatedName,
            updatedEmail,
            updatedPhone,
            updatedAddress
        );
        updatedCustomer.setCreatedAt(existingCustomer.getCreatedAt());
        
        Customer savedCustomer = customerRepository.update(customerId, updatedCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        return customerMapper.toResponseDto(savedCustomer);
    }
}
