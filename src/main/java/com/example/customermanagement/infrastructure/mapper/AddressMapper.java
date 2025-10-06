package com.example.customermanagement.infrastructure.mapper;

import com.example.customermanagement.domain.exception.AddressMappingException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.web.dto.address.AddressDTO;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            return new Address(
                    dto.getStreet(),
                    dto.getCity(),
                    dto.getState(),
                    dto.getZipCode(),
                    dto.getCountry()
            );
        } catch (Exception e) {
            throw new AddressMappingException(
                "Failed to map AddressDTO to Address entity: " + e.getMessage(), 
                dto, 
                e
            );
        }
    }

    public AddressDTO toDto(Address entity) {
        if (entity == null) {
            return null;
        }
        
        return new AddressDTO(
                entity.getStreet(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode(),
                entity.getCountry()
        );
    }
}
