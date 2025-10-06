package com.example.customermanagement.web.dto.customer;

import com.example.customermanagement.web.dto.address.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomerRequestDTO {
    @NotBlank(message = "Name is required and cannot be empty")
    private String name;

    @NotBlank(message = "Email is required and cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required and cannot be empty")
    private String phone;

    @NotNull(message = "Address is required")
    @Valid
    private AddressDTO address;

    public CustomerRequestDTO() {
    }

    public CustomerRequestDTO(String name, String email, String phone, AddressDTO address) {
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
}
