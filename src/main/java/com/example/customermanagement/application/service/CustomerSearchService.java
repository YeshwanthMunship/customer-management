package com.example.customermanagement.application.service;

import com.example.customermanagement.application.usecase.GetAllCustomersUseCase;
import com.example.customermanagement.application.usecase.SearchCustomersUseCase;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.infrastructure.mapper.CustomerSearchMapper;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerSearchService {
    
    private final GetAllCustomersUseCase getAllCustomersUseCase;
    private final SearchCustomersUseCase searchCustomersUseCase;
    private final CustomerSearchMapper customerSearchMapper;
    
    public CustomerSearchService(GetAllCustomersUseCase getAllCustomersUseCase,
                                 SearchCustomersUseCase searchCustomersUseCase,
                                 CustomerSearchMapper customerSearchMapper) {
        this.getAllCustomersUseCase = getAllCustomersUseCase;
        this.searchCustomersUseCase = searchCustomersUseCase;
        this.customerSearchMapper = customerSearchMapper;
    }
    
    public ResponseEntity<?> getAllCustomersWithFiltering(
            Integer page, Integer size, String search, String name, String email, String phone,
            String city, String state, String country, String zipCode, String createdAfter,
            String createdBefore, String updatedAfter, String updatedBefore, List<String> sort) {
        
        // Check if any filtering or sorting parameters are provided
        boolean hasFilters = search != null || name != null || email != null || phone != null ||
                           city != null || state != null || country != null || zipCode != null ||
                           createdAfter != null || createdBefore != null || 
                           updatedAfter != null || updatedBefore != null ||
                           (sort != null && !sort.isEmpty());
        
        // If no filters/sorting and no pagination, use simple method
        if (!hasFilters && page == null && size == null) {
            List<CustomerResponseDTO> customers = getAllCustomersUseCase.execute();
            return ResponseEntity.ok(customers);
        }
        
        // Build search criteria for filtering/sorting
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        
        CustomerSearchCriteria searchCriteria = customerSearchMapper.toSearchCriteria(
            search, name, email, phone, city, state, country, zipCode,
            createdAfter, createdBefore, updatedAfter, updatedBefore,
            sort, pageNumber, pageSize
        );
        
        if (page == null && size == null) {
            List<CustomerResponseDTO> customers = searchCustomersUseCase.executeAllResults(searchCriteria);
            return ResponseEntity.ok(customers);
        }
        
        PageResponseDTO<CustomerResponseDTO> pagedResponse = 
                searchCustomersUseCase.executeWithPagination(searchCriteria);
        
        return ResponseEntity.ok(pagedResponse);
    }
    

    public PageResponseDTO<CustomerResponseDTO> searchCustomers(
            String search, String name, String email, String phone, String city, String state, 
            String country, String zipCode, String createdAfter, String createdBefore, 
            String updatedAfter, String updatedBefore, List<String> sort, int page, int size) {
        
        CustomerSearchCriteria searchCriteria = customerSearchMapper.toSearchCriteria(
            search, name, email, phone, city, state, country, zipCode,
            createdAfter, createdBefore, updatedAfter, updatedBefore,
            sort, page, size
        );
        
        return searchCustomersUseCase.execute(searchCriteria);
    }
}
