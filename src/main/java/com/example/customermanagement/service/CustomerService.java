package com.example.customermanagement.service;

import com.example.customermanagement.domain.exception.CustomerNotFoundException;
import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.repository.CustomerRepository;
import com.example.customermanagement.mapper.CustomerMapper;
import com.example.customermanagement.mapper.CustomerSearchMapper;
import com.example.customermanagement.dto.common.PageResponseDTO;
import com.example.customermanagement.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.dto.customer.CustomerResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerSearchMapper customerSearchMapper;
    
    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           CustomerSearchMapper customerSearchMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchMapper = customerSearchMapper;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }
        Customer customer = customerMapper.toEntity(requestDTO);
        Customer created = customerRepository.save(customer);
        return customerMapper.toResponseDto(created);
    }

    public CustomerResponseDTO getCustomerById(UUID customerId) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return customerMapper.toResponseDto(customer);
    }

    public CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO requestDTO) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        if (requestDTO == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        Customer updatedEntity = customerMapper.toEntity(requestDTO);
        updatedEntity.setId(customerId);
        Customer saved = customerRepository.update(customerId, updatedEntity)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return customerMapper.toResponseDto(saved);
    }
    
    public CustomerResponseDTO patchCustomer(UUID customerId, CustomerPatchRequestDTO patchDTO) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        if (patchDTO == null) {
            throw InvalidCustomerDataException.emptyPatchRequest();
        }
        Customer existing = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        CustomerRequestDTO merged = new CustomerRequestDTO();
        merged.setName(patchDTO.getName() != null ? patchDTO.getName() : existing.getName());
        merged.setEmail(patchDTO.getEmail() != null ? patchDTO.getEmail() : existing.getEmail());
        merged.setPhone(patchDTO.getPhone() != null ? patchDTO.getPhone() : existing.getPhone());
        merged.setAddress(patchDTO.getAddress() != null ? patchDTO.getAddress() : customerMapper.toResponseDto(existing).getAddress());

        Customer updatedEntity = customerMapper.toEntity(merged);
        updatedEntity.setId(customerId);
        Customer saved = customerRepository.update(customerId, updatedEntity)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return customerMapper.toResponseDto(saved);
    }
    
    public void deleteCustomer(UUID customerId) {
        if (customerId == null) {
            throw InvalidCustomerDataException.nullCustomerId();
        }
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        customerRepository.deleteById(customerId);
    }

    public ResponseEntity<?> getAllCustomersWithFiltering(
            Integer page, Integer size, String search, String name, String email, String phone,
            String city, String state, String country, String zipCode, String createdAfter,
            String createdBefore, String updatedAfter, String updatedBefore, List<String> sort) {

        boolean hasFilters = search != null || name != null || email != null || phone != null ||
                city != null || state != null || country != null || zipCode != null ||
                createdAfter != null || createdBefore != null ||
                updatedAfter != null || updatedBefore != null ||
                (sort != null && !sort.isEmpty());

        if (!hasFilters && page == null && size == null) {
            List<CustomerResponseDTO> customers = getAllSortedByCreatedDesc();
            return ResponseEntity.ok(customers);
        }

        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        CustomerSearchCriteria searchCriteria = customerSearchMapper.toSearchCriteria(
                search, name, email, phone, city, state, country, zipCode,
                createdAfter, createdBefore, updatedAfter, updatedBefore,
                sort, pageNumber, pageSize
        );

        if (page == null && size == null) {
            List<CustomerResponseDTO> customers = executeSearchAllResults(searchCriteria);
            return ResponseEntity.ok(customers);
        }

        PageResponseDTO<CustomerResponseDTO> paged = executeSearchWithPagination(searchCriteria);
        return ResponseEntity.ok(paged);
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
        return executeSearch(searchCriteria);
    }

    private List<CustomerResponseDTO> getAllSortedByCreatedDesc() {
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return customerMapper.toResponseDtoList(customers);
    }

    private PageResponseDTO<CustomerResponseDTO> executeSearch(CustomerSearchCriteria criteria) {
        if (criteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        List<Customer> all = customerRepository.findAll();
        List<Customer> filtered = all.stream()
                .filter(c -> matchesSearchCriteria(c, criteria))
                .collect(Collectors.toList());
        if (criteria.hasSorting()) {
            filtered = applySorting(filtered, criteria);
        }
        int total = filtered.size();
        int startIndex = criteria.getPage() * criteria.getSize();
        int endIndex = Math.min(startIndex + criteria.getSize(), total);
        List<Customer> pageItems = startIndex < total ? filtered.subList(startIndex, endIndex) : List.of();
        List<CustomerResponseDTO> dtoList = pageItems.stream().map(customerMapper::toResponseDto).collect(Collectors.toList());
        return new PageResponseDTO<>(dtoList, criteria.getPage(), criteria.getSize(), total);
    }

    private PageResponseDTO<CustomerResponseDTO> executeSearchWithPagination(CustomerSearchCriteria criteria) {
        if (criteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        boolean hasFilters = hasAnyFilters(criteria);
        if (hasFilters) {
            return executeSearch(criteria);
        }
        return executeSimpleQuery(criteria.getPage(), criteria.getSize());
    }

    private List<CustomerResponseDTO> executeSearchAllResults(CustomerSearchCriteria criteria) {
        if (criteria == null) {
            throw InvalidCustomerDataException.nullSearchCriteria();
        }
        boolean hasFilters = hasAnyFilters(criteria);
        if (hasFilters) {
            return executeSearch(criteria).getContent();
        }
        return executeSimpleQueryWithoutPagination();
    }

    private PageResponseDTO<CustomerResponseDTO> executeSimpleQuery(int page, int size) {
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
        List<CustomerResponseDTO> dtoList = customerMapper.toResponseDtoList(customers);
        return new PageResponseDTO<>(dtoList, page, size, totalCount);
    }

    private List<CustomerResponseDTO> executeSimpleQueryWithoutPagination() {
        List<Customer> customers = customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return customerMapper.toResponseDtoList(customers);
    }

    private boolean hasAnyFilters(CustomerSearchCriteria criteria) {
        return criteria.hasSearchText() || criteria.hasFieldFilters() || criteria.hasDateFilters() || criteria.hasSorting();
    }

    private boolean matchesSearchCriteria(Customer customer, CustomerSearchCriteria criteria) {
        if (criteria.hasSearchText()) {
            boolean textMatch = isTextMatch(customer, criteria);
            if (!textMatch) {
                return false;
            }
        }

        if (matchesNullableField(customer.getName(), criteria.getName())) return false;
        if (matchesNullableField(customer.getEmail(), criteria.getEmail())) return false;
        if (matchesNullableField(customer.getPhone(), criteria.getPhone())) return false;
        if (matchesNullableField(customer.getAddress().getCity(), criteria.getCity())) return false;
        if (matchesNullableField(customer.getAddress().getState(), criteria.getState())) return false;
        if (matchesNullableField(customer.getAddress().getCountry(), criteria.getCountry())) return false;
        if (matchesNullableField(customer.getAddress().getZipCode(), criteria.getZipCode())) return false;

        return isDateInRange(customer.getCreatedAt(), criteria.getCreatedAfter(), criteria.getCreatedBefore()) &&
                isDateInRange(customer.getUpdatedAt(), criteria.getUpdatedAfter(), criteria.getUpdatedBefore());
    }

    private static boolean isTextMatch(Customer customer, CustomerSearchCriteria criteria) {
        String searchText = criteria.getSearchText().toLowerCase();
        return customer.getName().toLowerCase().contains(searchText) ||
                customer.getEmail().toLowerCase().contains(searchText) ||
                customer.getPhone().toLowerCase().contains(searchText) ||
                customer.getAddress().getCity().toLowerCase().contains(searchText) ||
                customer.getAddress().getState().toLowerCase().contains(searchText) ||
                customer.getAddress().getCountry().toLowerCase().contains(searchText);
    }

    private boolean matchesNullableField(String value, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return false;
        }
        return value == null || !value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean isDateInRange(java.time.LocalDateTime date, java.time.LocalDateTime after, java.time.LocalDateTime before) {
        if (after != null && date.isBefore(after)) {
            return false;
        }
        return before == null || !date.isAfter(before);
    }

    private List<Customer> applySorting(List<Customer> customers, CustomerSearchCriteria criteria) {
        return customers.stream()
                .sorted((c1, c2) -> {
                    for (CustomerSearchCriteria.SortCriteria sort : criteria.getSortCriteria()) {
                        int comparison = compareCustomers(c1, c2, sort.field());
                        if (comparison != 0) {
                            return sort.direction() == CustomerSearchCriteria.SortCriteria.SortDirection.DESC ? -comparison : comparison;
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
