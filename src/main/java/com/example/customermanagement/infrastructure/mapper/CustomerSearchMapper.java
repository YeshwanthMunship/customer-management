package com.example.customermanagement.infrastructure.mapper;

import com.example.customermanagement.domain.exception.InvalidDateFormatException;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.web.dto.customer.CustomerSearchRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomerSearchMapper {
    
    private static final Pattern SORT_PATTERN = Pattern.compile("^\\s*([a-zA-Z]+)\\s*,\\s*(asc|desc)\\s*$", Pattern.CASE_INSENSITIVE);
    
    public CustomerSearchCriteria toSearchCriteria(CustomerSearchRequestDTO requestDTO) {
        if (requestDTO == null) {
            return CustomerSearchCriteria.builder().build();
        }
        
        return buildSearchCriteria(requestDTO);
    }
    
    public CustomerSearchCriteria toSearchCriteria(
            String search, String name, String email, String phone,
            String city, String state, String country, String zipCode,
            String createdAfter, String createdBefore, 
            String updatedAfter, String updatedBefore,
            List<String> sort, int page, int size) {
        
        CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
        requestDTO.setSearch(search);
        requestDTO.setName(name);
        requestDTO.setEmail(email);
        requestDTO.setPhone(phone);
        requestDTO.setCity(city);
        requestDTO.setState(state);
        requestDTO.setCountry(country);
        requestDTO.setZipCode(zipCode);
        requestDTO.setCreatedAfterString(createdAfter);
        requestDTO.setCreatedBeforeString(createdBefore);
        requestDTO.setUpdatedAfterString(updatedAfter);
        requestDTO.setUpdatedBeforeString(updatedBefore);
        requestDTO.setSort(sort);
        requestDTO.setPage(page);
        requestDTO.setSize(size);
        
        return buildSearchCriteria(requestDTO);
    }
    
    private CustomerSearchCriteria buildSearchCriteria(CustomerSearchRequestDTO requestDTO) {
        
        CustomerSearchCriteria.Builder builder = CustomerSearchCriteria.builder()
                .searchText(sanitizeString(requestDTO.getSearch()))
                .name(sanitizeString(requestDTO.getName()))
                .email(sanitizeString(requestDTO.getEmail()))
                .phone(sanitizeString(requestDTO.getPhone()))
                .city(sanitizeString(requestDTO.getCity()))
                .state(sanitizeString(requestDTO.getState()))
                .country(sanitizeString(requestDTO.getCountry()))
                .zipCode(sanitizeString(requestDTO.getZipCode()))
                .createdAfter(parseDateTime(requestDTO.getCreatedAfterString()))
                .createdBefore(parseDateTime(requestDTO.getCreatedBeforeString()))
                .updatedAfter(parseDateTime(requestDTO.getUpdatedAfterString()))
                .updatedBefore(parseDateTime(requestDTO.getUpdatedBeforeString()))
                .page(requestDTO.getPage())
                .size(requestDTO.getSize());
        
        if (requestDTO.getSort() != null && !requestDTO.getSort().isEmpty()) {
            List<CustomerSearchCriteria.SortCriteria> sortCriteria = parseSortCriteria(requestDTO.getSort());
            builder.sortCriteria(sortCriteria);
        }
        
        return builder.build();
    }
    
    private String sanitizeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }
    
    private List<CustomerSearchCriteria.SortCriteria> parseSortCriteria(List<String> sortStrings) {
        List<CustomerSearchCriteria.SortCriteria> sortCriteria = new ArrayList<>();
        
        for (String sortString : sortStrings) {
            if (sortString == null || sortString.trim().isEmpty()) {
                continue;
            }
            
            CustomerSearchCriteria.SortCriteria criteria = parseSingleSortCriteria(sortString.trim());
            if (criteria != null) {
                sortCriteria.add(criteria);
            }
        }
        
        return sortCriteria;
    }
    
    private CustomerSearchCriteria.SortCriteria parseSingleSortCriteria(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = sortString.trim();
        
        if (SORT_PATTERN.matcher(trimmed).matches()) {
            String[] parts = trimmed.split(",");
            String field = parts[0].trim();
            String direction = parts[1].trim().toLowerCase();
            
            if (!isValidSortField(field)) {
                return null;
            }
            
            CustomerSearchCriteria.SortCriteria.SortDirection sortDirection = 
                "desc".equals(direction) ? 
                    CustomerSearchCriteria.SortCriteria.SortDirection.DESC : 
                    CustomerSearchCriteria.SortCriteria.SortDirection.ASC;
            
            return new CustomerSearchCriteria.SortCriteria(normalizeFieldName(field), sortDirection);
        }
        
        // Check if it's just a field name without direction (defaults to ASC)
        if (isValidSortField(trimmed)) {
            return new CustomerSearchCriteria.SortCriteria(
                normalizeFieldName(trimmed), 
                CustomerSearchCriteria.SortCriteria.SortDirection.ASC
            );
        }
        
        return null;
    }
    
    private boolean isValidSortField(String field) {
        if (field == null || field.trim().isEmpty()) {
            return false;
        }
        
        String normalizedField = field.toLowerCase().trim();
        return switch (normalizedField) {
            case "name", "email", "phone", "city", "state", "country", 
                 "zipcode", "zip", "createdat", "created", "updatedat", "updated" -> true;
            default -> false;
        };
    }
    
    private String normalizeFieldName(String field) {
        return switch (field.toLowerCase().trim()) {
            case "zip" -> "zipcode";
            case "created" -> "createdat";
            case "updated" -> "updatedat";
            default -> field.toLowerCase().trim();
        };
    }
    
    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateTimeString.trim());
        } catch (DateTimeParseException e) {
            throw InvalidDateFormatException.invalidDateTimeFormat(dateTimeString, e);
        }
    }
}
