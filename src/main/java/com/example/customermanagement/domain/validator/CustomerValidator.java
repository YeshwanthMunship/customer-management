package com.example.customermanagement.domain.validator;


import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Address;

public class CustomerValidator {
    
    public static void validateCustomerData(String name, String email, String phone, Address address) {
        validateName(name);
        validateEmail(email);
        validatePhone(phone);
        validateAddress(address);
    }
    
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw InvalidCustomerDataException.nullOrEmptyField("name", name);
        }
    }
    
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw InvalidCustomerDataException.nullOrEmptyField("email", email);
        }
        EmailValidator.validateEmail(email);
    }
    
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw InvalidCustomerDataException.nullOrEmptyField("phone", phone);
        }
    }
    
    public static void validateAddress(Address address) {
        if (address == null) {
            throw InvalidCustomerDataException.nullField("address");
        }
    }
    
    public static CustomerData normalizeCustomerData(String name, String email, String phone) {
        return new CustomerData(
            name != null ? name.trim() : null,
            email != null ? email.trim().toLowerCase() : null,
            phone != null ? phone.trim() : null
        );
    }
    
    
    public record CustomerData(String name, String email, String phone) {
    }
}
