package com.example.customermanagement.web.dto.customer;

import com.example.customermanagement.web.dto.address.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public class CustomerPatchRequestDTO {
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @Valid
    private AddressDTO address;

    public CustomerPatchRequestDTO() {
    }

    public CustomerPatchRequestDTO(String name, String email, String phone, AddressDTO address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public boolean hasAnyField() {
        return name != null || email != null || phone != null || address != null;
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasPhone() {
        return phone != null;
    }

    public boolean hasAddress() {
        return address != null;
    }
}
