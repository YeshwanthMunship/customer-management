package com.example.customermanagement.application.facade;

import com.example.customermanagement.application.service.CustomerSearchService;
import com.example.customermanagement.application.service.CustomerService;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomerFacade {
    
    private final CustomerService customerService;
    private final CustomerSearchService customerSearchService;
    
    public CustomerFacade(CustomerService customerService,
                          CustomerSearchService customerSearchService) {
        this.customerService = customerService;
        this.customerSearchService = customerSearchService;
    }
        
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        return customerService.createCustomer(requestDTO);
    }
    
    public CustomerResponseDTO getCustomerById(UUID customerId) {
        return customerService.getCustomerById(customerId);
    }
    
    public CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO requestDTO) {
        return customerService.updateCustomer(customerId, requestDTO);
    }
    
    public CustomerResponseDTO patchCustomer(UUID customerId, CustomerPatchRequestDTO patchDTO) {
        return customerService.patchCustomer(customerId, patchDTO);
    }
    
    public void deleteCustomer(UUID customerId) {
        customerService.deleteCustomer(customerId);
    }
    
   
    public ResponseEntity<?> getAllCustomersWithFiltering(
            Integer page, Integer size, String search, String name, String email, String phone,
            String city, String state, String country, String zipCode, String createdAfter,
            String createdBefore, String updatedAfter, String updatedBefore, List<String> sort) {
        
        return customerSearchService.getAllCustomersWithFiltering(
            page, size, search, name, email, phone, city, state, country, zipCode,
            createdAfter, createdBefore, updatedAfter, updatedBefore, sort
        );
    }
    

    public PageResponseDTO<CustomerResponseDTO> searchCustomers(
            String search, String name, String email, String phone, String city, String state, 
            String country, String zipCode, String createdAfter, String createdBefore, 
            String updatedAfter, String updatedBefore, List<String> sort, int page, int size) {
        
        return customerSearchService.searchCustomers(
            search, name, email, phone, city, state, country, zipCode,
            createdAfter, createdBefore, updatedAfter, updatedBefore, sort, page, size
        );
    }
}
