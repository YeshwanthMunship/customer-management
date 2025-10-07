package com.example.customermanagement.mapper;

import com.example.customermanagement.domain.exception.CustomerMappingException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.dto.address.AddressDTO;
import com.example.customermanagement.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.dto.customer.CustomerResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    
    private final AddressMapper addressMapper;
    
    public CustomerMapper(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public Customer toEntity(CustomerRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            Address address = addressMapper.toEntity(dto.getAddress());
            
            return new Customer(
                    dto.getName(),
                    dto.getEmail(),
                    dto.getPhone(),
                    address
            );
        } catch (Exception e) {
            throw new CustomerMappingException(
                "Failed to map CustomerRequestDTO to Customer entity: " + e.getMessage(), 
                dto, 
                e
            );
        }
    }

    public CustomerResponseDTO toResponseDto(Customer entity) {
        if (entity == null) {
            return null;
        }
        
        try {
            AddressDTO addressDTO = addressMapper.toDto(entity.getAddress());
            
            return new CustomerResponseDTO(
                    entity.getId(),
                    entity.getName(),
                    entity.getEmail(),
                    entity.getPhone(),
                    addressDTO,
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
            );
        } catch (Exception e) {
            throw new CustomerMappingException(
                "Failed to map Customer entity to CustomerResponseDTO: " + e.getMessage(), 
                entity, 
                e
            );
        }
    }

    public List<CustomerResponseDTO> toResponseDtoList(List<Customer> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
