package com.example.customermanagement.repository;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {
    
    private final ConcurrentHashMap<UUID, Customer> dataStore = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {
        if (customer == null) {
            throw InvalidCustomerDataException.nullCustomer();
        }
        dataStore.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(dataStore.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    @Override
    public Optional<Customer> update(UUID id, Customer customer) {
        if (id == null || customer == null) {
            return Optional.empty();
        }
        
        if (!dataStore.containsKey(id)) {
            return Optional.empty();
        }
        
        customer.setId(id);
        customer.updateTimestamp();
        dataStore.put(id, customer);
        return Optional.of(customer);
    }

    @Override
    public boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }
        return dataStore.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            return false;
        }
        return dataStore.containsKey(id);
    }

    @Override
    public long count() {
        return dataStore.size();
    }
}
