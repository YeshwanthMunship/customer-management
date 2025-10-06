package com.example.customermanagement.domain.model;


import com.example.customermanagement.domain.validator.CustomerValidator;

import java.util.UUID;

public class Customer extends BaseEntity {
    private String name;
    private String email;
    private String phone;
    private Address address;

    public Customer(String name, String email, String phone, Address address) {
        super();
        setProperties(name, email, phone, address);
    }

    public Customer(UUID id, String name, String email, String phone, Address address) {
        super(id);
        setProperties(name, email, phone, address);
    }

    private void setProperties(String name, String email, String phone, Address address) {
        CustomerValidator.validateCustomerData(name, email, phone, address);
    
        CustomerValidator.CustomerData normalizedData = CustomerValidator.normalizeCustomerData(name, email, phone);
        this.name = normalizedData.name();
        this.email = normalizedData.email();
        this.phone = normalizedData.phone();
        this.address = address;
    }

    public void updateInfo(String name, String email, String phone, Address address) {
        setProperties(name, email, phone, address);
        updateTimestamp();
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
