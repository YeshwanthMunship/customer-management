package com.example.customermanagement.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerSearchCriteria {
    
    private final String searchText;
    private final String name;
    private final String email;
    private final String phone;
    private final String city;
    private final String state;
    private final String country;
    private final String zipCode;
    private final LocalDateTime createdAfter;
    private final LocalDateTime createdBefore;
    private final LocalDateTime updatedAfter;
    private final LocalDateTime updatedBefore;
    private final List<SortCriteria> sortCriteria;
    private final int page;
    private final int size;
    
    private CustomerSearchCriteria(Builder builder) {
        this.searchText = builder.searchText;
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.city = builder.city;
        this.state = builder.state;
        this.country = builder.country;
        this.zipCode = builder.zipCode;
        this.createdAfter = builder.createdAfter;
        this.createdBefore = builder.createdBefore;
        this.updatedAfter = builder.updatedAfter;
        this.updatedBefore = builder.updatedBefore;
        this.sortCriteria = builder.sortCriteria != null ? List.copyOf(builder.sortCriteria) : List.of();
        this.page = Math.max(0, builder.page);
        this.size = Math.max(1, Math.min(100, builder.size));
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getSearchText() { return searchText; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }
    public LocalDateTime getCreatedAfter() { return createdAfter; }
    public LocalDateTime getCreatedBefore() { return createdBefore; }
    public LocalDateTime getUpdatedAfter() { return updatedAfter; }
    public LocalDateTime getUpdatedBefore() { return updatedBefore; }
    public List<SortCriteria> getSortCriteria() { return sortCriteria; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    
    public boolean hasSearchText() {
        return searchText != null && !searchText.isBlank();
    }
    
    public boolean hasFieldFilters() {
        return name != null || email != null || phone != null || 
               city != null || state != null || country != null || zipCode != null;
    }
    
    public boolean hasDateFilters() {
        return createdAfter != null || createdBefore != null || 
               updatedAfter != null || updatedBefore != null;
    }
    
    public boolean hasSorting() {
        return !sortCriteria.isEmpty();
    }
    
    public static class Builder {
        private String searchText;
        private String name;
        private String email;
        private String phone;
        private String city;
        private String state;
        private String country;
        private String zipCode;
        private LocalDateTime createdAfter;
        private LocalDateTime createdBefore;
        private LocalDateTime updatedAfter;
        private LocalDateTime updatedBefore;
        private List<SortCriteria> sortCriteria;
        private int page = 0;
        private int size = 20;
        
        public Builder searchText(String searchText) {
            this.searchText = searchText;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder city(String city) {
            this.city = city;
            return this;
        }
        
        public Builder state(String state) {
            this.state = state;
            return this;
        }
        
        public Builder country(String country) {
            this.country = country;
            return this;
        }
        
        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }
        
        public Builder createdAfter(LocalDateTime createdAfter) {
            this.createdAfter = createdAfter;
            return this;
        }
        
        public Builder createdBefore(LocalDateTime createdBefore) {
            this.createdBefore = createdBefore;
            return this;
        }
        
        public Builder updatedAfter(LocalDateTime updatedAfter) {
            this.updatedAfter = updatedAfter;
            return this;
        }
        
        public Builder updatedBefore(LocalDateTime updatedBefore) {
            this.updatedBefore = updatedBefore;
            return this;
        }
        
        public Builder sortCriteria(List<SortCriteria> sortCriteria) {
            this.sortCriteria = sortCriteria;
            return this;
        }
        
        public Builder page(int page) {
            this.page = page;
            return this;
        }
        
        public Builder size(int size) {
            this.size = size;
            return this;
        }
        
        public CustomerSearchCriteria build() {
            return new CustomerSearchCriteria(this);
        }
    }
    
    public record SortCriteria(String field, SortDirection direction) {
        public enum SortDirection {
            ASC, DESC
        }
    }
}
