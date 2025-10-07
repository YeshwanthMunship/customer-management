package com.example.customermanagement.dto.customer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for customer search requests with validation.
 * Supports text search, field filters, date ranges, sorting, and pagination.
 */
public class CustomerSearchRequestDTO {
    
    private String search;
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
    
    // String versions for direct parameter binding
    private String createdAfterString;
    private String createdBeforeString;
    private String updatedAfterString;
    private String updatedBeforeString;
    private List<String> sort;
    
    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 20;
    
    // Constructors
    public CustomerSearchRequestDTO() {}
    
    // Getters and Setters
    public String getSearch() {
        return search;
    }
    
    public void setSearch(String search) {
        this.search = search;
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
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public LocalDateTime getCreatedAfter() {
        return createdAfter;
    }
    
    public void setCreatedAfter(LocalDateTime createdAfter) {
        this.createdAfter = createdAfter;
    }
    
    public LocalDateTime getCreatedBefore() {
        return createdBefore;
    }
    
    public void setCreatedBefore(LocalDateTime createdBefore) {
        this.createdBefore = createdBefore;
    }
    
    public LocalDateTime getUpdatedAfter() {
        return updatedAfter;
    }
    
    public void setUpdatedAfter(LocalDateTime updatedAfter) {
        this.updatedAfter = updatedAfter;
    }
    
    public LocalDateTime getUpdatedBefore() {
        return updatedBefore;
    }
    
    public void setUpdatedBefore(LocalDateTime updatedBefore) {
        this.updatedBefore = updatedBefore;
    }
    
    public List<String> getSort() {
        return sort;
    }
    
    public void setSort(List<String> sort) {
        this.sort = sort;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public String getCreatedAfterString() {
        return createdAfterString;
    }
    
    public void setCreatedAfterString(String createdAfterString) {
        this.createdAfterString = createdAfterString;
    }
    
    public String getCreatedBeforeString() {
        return createdBeforeString;
    }
    
    public void setCreatedBeforeString(String createdBeforeString) {
        this.createdBeforeString = createdBeforeString;
    }
    
    public String getUpdatedAfterString() {
        return updatedAfterString;
    }
    
    public void setUpdatedAfterString(String updatedAfterString) {
        this.updatedAfterString = updatedAfterString;
    }
    
    public String getUpdatedBeforeString() {
        return updatedBeforeString;
    }
    
    public void setUpdatedBeforeString(String updatedBeforeString) {
        this.updatedBeforeString = updatedBeforeString;
    }
    
    @Override
    public String toString() {
        return "CustomerSearchRequestDTO{" +
                "search='" + search + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", createdAfter=" + createdAfter +
                ", createdBefore=" + createdBefore +
                ", updatedAfter=" + updatedAfter +
                ", updatedBefore=" + updatedBefore +
                ", sort=" + sort +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
