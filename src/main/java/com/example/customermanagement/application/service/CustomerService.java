package com.example.customermanagement.application.service;

import com.example.customermanagement.application.usecase.*;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {
    
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final PatchCustomerUseCase patchCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    
    public CustomerService(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            PatchCustomerUseCase patchCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.patchCustomerUseCase = patchCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        return createCustomerUseCase.execute(requestDTO);
    }

    public CustomerResponseDTO getCustomerById(UUID customerId) {
        return getCustomerUseCase.execute(customerId);
    }

    public CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO requestDTO) {
        return updateCustomerUseCase.execute(customerId, requestDTO);
    }
    
    public CustomerResponseDTO patchCustomer(UUID customerId, CustomerPatchRequestDTO patchDTO) {
        return patchCustomerUseCase.execute(customerId, patchDTO);
    }
    
    public void deleteCustomer(UUID customerId) {
        deleteCustomerUseCase.execute(customerId);
    }

}
