package com.example.customermanagement.domain.validator;


import com.example.customermanagement.domain.exception.InvalidAddressException;

public class AddressValidator {
    

    public static void validateAddressData(String street, String city, String state, String zipCode, String country) {
        validateStreet(street);
        validateCity(city);
        validateState(state);
        validateZipCode(zipCode);
        validateCountry(country);
    }
    
    public static void validateStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            throw InvalidAddressException.nullOrEmpty("street");
        }
    }
    
    public static void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw InvalidAddressException.nullOrEmpty("city");
        }
    }
    
    public static void validateState(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw InvalidAddressException.nullOrEmpty("state");
        }
    }
    

    public static void validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw InvalidAddressException.nullOrEmpty("zipCode");
        }
    }
    
    public static void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw InvalidAddressException.nullOrEmpty("country");
        }
    }
    
    public static AddressData normalizeAddressData(String street, String city, String state, String zipCode, String country) {
        return new AddressData(
            street != null ? street.trim() : null,
            city != null ? city.trim() : null,
            state != null ? state.trim() : null,
            zipCode != null ? zipCode.trim() : null,
            country != null ? country.trim() : null
        );
    }
    
    public record AddressData(String street, String city, String state, String zipCode, String country) {
    }
}
