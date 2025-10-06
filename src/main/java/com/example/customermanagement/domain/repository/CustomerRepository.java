package com.example.customermanagement.domain.repository;


import com.example.customermanagement.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    List<Customer> findAll();

    Optional<Customer> update(UUID id, Customer customer);

    boolean deleteById(UUID id);

    boolean existsById(UUID id);

    long count();
}
