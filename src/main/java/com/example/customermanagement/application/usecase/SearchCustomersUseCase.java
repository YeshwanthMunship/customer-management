package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchCustomersUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    public SearchCustomersUseCase(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    public PageResponseDTO<CustomerResponseDTO> execute(CustomerSearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        
        List<Customer> allCustomers = customerRepository.findAll();
        
        List<Customer> filteredCustomers = allCustomers.stream()
                .filter(customer -> matchesSearchCriteria(customer, searchCriteria))
                .collect(Collectors.toList());
        
        if (searchCriteria.hasSorting()) {
            filteredCustomers = applySorting(filteredCustomers, searchCriteria);
        }
        
        int totalElements = filteredCustomers.size();

        int startIndex = searchCriteria.getPage() * searchCriteria.getSize();
        int endIndex = Math.min(startIndex + searchCriteria.getSize(), totalElements);
        
        List<Customer> paginatedCustomers = startIndex < totalElements ? 
            filteredCustomers.subList(startIndex, endIndex) : List.of();
        
        List<CustomerResponseDTO> customerDTOs = paginatedCustomers.stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                customerDTOs,
                searchCriteria.getPage(),
                searchCriteria.getSize(),
                totalElements
        );
    }
    

    public PageResponseDTO<CustomerResponseDTO> executeWithPagination(CustomerSearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        
        boolean hasFilters = hasAnyFilters(searchCriteria);
        
        if (hasFilters) {
            return execute(searchCriteria);
        } else {
            return executeSimpleQuery(searchCriteria.getPage(), searchCriteria.getSize());
        }
    }
    
    public List<CustomerResponseDTO> executeAllResults(CustomerSearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        
        boolean hasFilters = hasAnyFilters(searchCriteria);
        
        if (hasFilters) {
            CustomerSearchCriteria unpaginatedCriteria = CustomerSearchCriteria.builder()
                    .searchText(searchCriteria.getSearchText())
                    .name(searchCriteria.getName())
                    .email(searchCriteria.getEmail())
                    .phone(searchCriteria.getPhone())
                    .city(searchCriteria.getCity())
                    .state(searchCriteria.getState())
                    .country(searchCriteria.getCountry())
                    .zipCode(searchCriteria.getZipCode())
                    .createdAfter(searchCriteria.getCreatedAfter())
                    .createdBefore(searchCriteria.getCreatedBefore())
                    .updatedAfter(searchCriteria.getUpdatedAfter())
                    .updatedBefore(searchCriteria.getUpdatedBefore())
                    .sortCriteria(searchCriteria.getSortCriteria())
                    .page(searchCriteria.getPage())
                    .size(searchCriteria.getSize())
                    .build();
            
            return execute(unpaginatedCriteria).getContent();
        } else {
            return executeSimpleQueryWithoutPagination();
        }
    }
    

    private PageResponseDTO<CustomerResponseDTO> executeSimpleQuery(int page, int size) {
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
        
        long totalCount = customerRepository.count();
        List<CustomerResponseDTO> customerDTOs = customerMapper.toResponseDtoList(customers);
        return new PageResponseDTO<>(customerDTOs, page, size, totalCount);
    }
    

    private List<CustomerResponseDTO> executeSimpleQueryWithoutPagination() {
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return customerMapper.toResponseDtoList(customers);
    }
    
    private boolean hasAnyFilters(CustomerSearchCriteria criteria) {
        return criteria.hasSearchText() || 
               criteria.hasFieldFilters() || 
               criteria.hasDateFilters() || 
               criteria.hasSorting();
    }
    
    private boolean matchesSearchCriteria(Customer customer, CustomerSearchCriteria criteria) {
        if (criteria.hasSearchText()) {
            boolean matchesText = isMatchesText(customer, criteria);

            if (!matchesText) {
                return false;
            }
        }
        
        if (matchesField(customer.getName(), criteria.getName()) ||
                matchesField(customer.getEmail(), criteria.getEmail()) ||
                matchesField(customer.getPhone(), criteria.getPhone()) ||
                matchesField(customer.getAddress().getCity(), criteria.getCity()) ||
                matchesField(customer.getAddress().getState(), criteria.getState()) ||
                matchesField(customer.getAddress().getCountry(), criteria.getCountry()) ||
                matchesField(customer.getAddress().getZipCode(), criteria.getZipCode())) {
            return false;
        }

        return isDateInRange(customer.getCreatedAt(), criteria.getCreatedAfter(), criteria.getCreatedBefore()) &&
                isDateInRange(customer.getUpdatedAt(), criteria.getUpdatedAfter(), criteria.getUpdatedBefore());
    }


    private boolean matchesField(String customerFieldValue, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return false;
        }
        return customerFieldValue == null || !customerFieldValue.toLowerCase().contains(searchTerm.toLowerCase());
    }
    
  
    private boolean isDateInRange(java.time.LocalDateTime date, java.time.LocalDateTime after, java.time.LocalDateTime before) {
        if (after != null && date.isBefore(after)) {
            return false;
        }
        return before == null || !date.isAfter(before);
    }

    private static boolean isMatchesText(Customer customer, CustomerSearchCriteria criteria) {
        String searchText = criteria.getSearchText().toLowerCase();
        return customer.getName().toLowerCase().contains(searchText) ||
                            customer.getEmail().toLowerCase().contains(searchText) ||
                            customer.getPhone().toLowerCase().contains(searchText) ||
                            customer.getAddress().getCity().toLowerCase().contains(searchText) ||
                            customer.getAddress().getState().toLowerCase().contains(searchText) ||
                            customer.getAddress().getCountry().toLowerCase().contains(searchText);
    }

    private List<Customer> applySorting(List<Customer> customers, CustomerSearchCriteria criteria) {
        return customers.stream()
                .sorted((c1, c2) -> {
                    for (CustomerSearchCriteria.SortCriteria sort : criteria.getSortCriteria()) {
                        int comparison = compareCustomers(c1, c2, sort.field());
                        if (comparison != 0) {
                            return sort.direction() == CustomerSearchCriteria.SortCriteria.SortDirection.DESC ? 
                                -comparison : comparison;
                        }
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }
    
    private int compareCustomers(Customer c1, Customer c2, String field) {
        return switch (field.toLowerCase()) {
            case "name" -> c1.getName().compareToIgnoreCase(c2.getName());
            case "email" -> c1.getEmail().compareToIgnoreCase(c2.getEmail());
            case "phone" -> c1.getPhone().compareToIgnoreCase(c2.getPhone());
            case "city" -> c1.getAddress().getCity().compareToIgnoreCase(c2.getAddress().getCity());
            case "state" -> c1.getAddress().getState().compareToIgnoreCase(c2.getAddress().getState());
            case "country" -> c1.getAddress().getCountry().compareToIgnoreCase(c2.getAddress().getCountry());
            case "zipcode", "zip" -> c1.getAddress().getZipCode().compareToIgnoreCase(c2.getAddress().getZipCode());
            case "createdat", "created" -> c1.getCreatedAt().compareTo(c2.getCreatedAt());
            case "updatedat", "updated" -> c1.getUpdatedAt().compareTo(c2.getUpdatedAt());
            default -> 0;
        };
    }
}
