package com.example.customermanagement.performance;

import com.example.customermanagement.application.usecase.SearchCustomersUseCase;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EnabledIfSystemProperty(named = "run.performance.tests", matches = "true")
@DisplayName("Search Performance Tests")
class SearchPerformanceTest {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    private SearchCustomersUseCase searchCustomersUseCase;
    
    private static final int LARGE_DATASET_SIZE = 100000; // 100K records for stress testing
    private static final long ACCEPTABLE_SEARCH_TIME_MS = 2000; // 2 seconds for 100K records
    private static final long ACCEPTABLE_PAGINATION_TIME_MS = 1000; // 1 second for pagination
    
    @BeforeEach
    void setUp() {
        searchCustomersUseCase = new SearchCustomersUseCase(customerRepository, customerMapper);
        
        createLargeDataset();
    }
    
    @Test
    @DisplayName("Should perform text search efficiently on large dataset")
    void shouldPerformTextSearchEfficientlyOnLargeDataset() {
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .searchText("john")
            .build();
        
        long startTime = System.currentTimeMillis();
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertNotNull(result);
        assertTrue(executionTime < ACCEPTABLE_SEARCH_TIME_MS, 
            String.format("Search took %d ms, expected less than %d ms", executionTime, ACCEPTABLE_SEARCH_TIME_MS));
        
        System.out.printf("Text search on %d records completed in %d ms%n", 
            LARGE_DATASET_SIZE, executionTime);
    }
    
    @Test
    @DisplayName("Should perform field filtering efficiently on large dataset")
    void shouldPerformFieldFilteringEfficientlyOnLargeDataset() {
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .state("CA")
            .city("Los Angeles")
            .build();
        
        long startTime = System.currentTimeMillis();
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertNotNull(result);
        assertTrue(executionTime < ACCEPTABLE_SEARCH_TIME_MS,
            String.format("Field filtering took %d ms, expected less than %d ms", executionTime, ACCEPTABLE_SEARCH_TIME_MS));
        
        System.out.printf("Field filtering on %d records completed in %d ms%n", 
            LARGE_DATASET_SIZE, executionTime);
    }
    
    @Test
    @DisplayName("Should perform sorting efficiently on large dataset")
    void shouldPerformSortingEfficientlyOnLargeDataset() {
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .sortCriteria(List.of(
                new CustomerSearchCriteria.SortCriteria("name", 
                    CustomerSearchCriteria.SortCriteria.SortDirection.ASC),
                new CustomerSearchCriteria.SortCriteria("email", 
                    CustomerSearchCriteria.SortCriteria.SortDirection.DESC)
            ))
            .build();
        
        long startTime = System.currentTimeMillis();
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertNotNull(result);
        assertTrue(executionTime < ACCEPTABLE_SEARCH_TIME_MS,
            String.format("Sorting took %d ms, expected less than %d ms", executionTime, ACCEPTABLE_SEARCH_TIME_MS));
        
        System.out.printf("Sorting %d records completed in %d ms%n", 
            LARGE_DATASET_SIZE, executionTime);
    }
    
    @Test
    @DisplayName("Should perform pagination efficiently on large dataset")
    void shouldPerformPaginationEfficientlyOnLargeDataset() {
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .page(100) // Deep pagination
            .size(50)
            .build();
        
        long startTime = System.currentTimeMillis();
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertNotNull(result);
        assertTrue(executionTime < ACCEPTABLE_PAGINATION_TIME_MS,
            String.format("Pagination took %d ms, expected less than %d ms", executionTime, ACCEPTABLE_PAGINATION_TIME_MS));
        
        System.out.printf("Pagination (page 100) on %d records completed in %d ms%n", 
            LARGE_DATASET_SIZE, executionTime);
    }
    
    @Test
    @DisplayName("Should perform complex search efficiently on large dataset")
    void shouldPerformComplexSearchEfficientlyOnLargeDataset() {
        LocalDateTime filterDate = LocalDateTime.now().minusDays(30);
        
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .searchText("test")
            .state("CA")
            .createdAfter(filterDate)
            .sortCriteria(List.of(
                new CustomerSearchCriteria.SortCriteria("createdAt", 
                    CustomerSearchCriteria.SortCriteria.SortDirection.DESC)
            ))
            .page(0)
            .size(20)
            .build();
        
        long startTime = System.currentTimeMillis();
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertNotNull(result);
        assertTrue(executionTime < ACCEPTABLE_SEARCH_TIME_MS,
            String.format("Complex search took %d ms, expected less than %d ms", executionTime, ACCEPTABLE_SEARCH_TIME_MS));
        
        System.out.printf("Complex search on %d records completed in %d ms%n", 
            LARGE_DATASET_SIZE, executionTime);
    }
    
    @Test
    @DisplayName("Should handle memory efficiently with large result sets")
    void shouldHandleMemoryEfficientlyWithLargeResultSets() {
        // Get memory before operation
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Suggest garbage collection
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .page(0)
            .size(1000) // Large page size
            .build();
        
        PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
        
        // Get memory after operation
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        assertNotNull(result);
        // Note: Due to pagination limits, we get 100 results instead of 1000
        assertEquals(100, result.getContent().size());
        
        // Memory usage should be reasonable (less than 100MB for 100 records from 100K dataset)
        long maxAcceptableMemory = 100 * 1024 * 1024; // 100MB
        assertTrue(memoryUsed < maxAcceptableMemory,
            String.format("Memory usage %d bytes exceeded acceptable limit %d bytes", 
                memoryUsed, maxAcceptableMemory));
        
        System.out.printf("Memory usage for 100 records: %d KB%n", memoryUsed / 1024);
    }
    
    @Test
    @DisplayName("Should maintain consistent performance across multiple searches")
    void shouldMaintainConsistentPerformanceAcrossMultipleSearches() {
        List<Long> executionTimes = new ArrayList<>();
        int numberOfSearches = 10;
        
        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
            .searchText("customer")
            .page(0)
            .size(100)
            .build();
        
        // Warm up
        searchCustomersUseCase.execute(criteria);
        
        // Perform multiple searches and measure times
        for (int i = 0; i < numberOfSearches; i++) {
            long startTime = System.currentTimeMillis();
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            long endTime = System.currentTimeMillis();
            
            executionTimes.add(endTime - startTime);
            assertNotNull(result);
        }
        
        // Calculate statistics
        double averageTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxTime = executionTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minTime = executionTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        // Performance should be consistent (max time shouldn't be more than 3x average)
        assertTrue(maxTime <= averageTime * 3,
            String.format("Performance inconsistent: max=%d ms, avg=%.2f ms", maxTime, averageTime));
        
        System.out.printf("Performance over %d searches: avg=%.2f ms, min=%d ms, max=%d ms%n",
            numberOfSearches, averageTime, minTime, maxTime);
    }
    
    private void createLargeDataset() {
        System.out.println("Creating large dataset for performance testing...");
        
        Random random = new Random(12345); // Fixed seed for reproducible results
        String[] firstNames = {"Rajesh", "Priya", "Amit", "Anita", "Chandra", "Deepa", "Esha", "Firoz", "Gita", "Hari"};
        String[] lastNames = {"Kumar", "Sharma", "Patel", "Singh", "Gupta", "Agarwal", "Verma", "Yadav", "Reddy", "Joshi"};
        String[] cities = {"Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune", "Ahmedabad", "Jaipur", "Surat"};
        String[] states = {"Maharashtra", "Delhi", "Karnataka", "Telangana", "Tamil Nadu", "West Bengal", "Maharashtra", "Gujarat", "Rajasthan", "Gujarat"};
        String[] domains = {"example.com", "test.org", "company.net", "business.com", "service.org"};
        
        List<Customer> customers = new ArrayList<>();
        
        for (int i = 0; i < LARGE_DATASET_SIZE; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String name = firstName + " " + lastName;
            
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + 
                          i + "@" + domains[random.nextInt(domains.length)];
            
            String phone = "+91-" + String.format("%010d", random.nextInt(1000000000));
            
            int cityIndex = random.nextInt(cities.length);
            String city = cities[cityIndex];
            String state = states[cityIndex];
            String zipCode = String.format("%05d", random.nextInt(100000));
            
            Address address = new Address(
                (i + 1) + " Test Street",
                city,
                state,
                zipCode,
                "India"
            );
            
            Customer customer = new Customer(name, email, phone, address);
            customers.add(customer);
            
            // Save in batches to avoid memory issues
            if (customers.size() >= 1000) {
                customers.forEach(customerRepository::save);
                customers.clear();
            }
        }
        
        // Save remaining customers
        customers.forEach(customerRepository::save);
        
        System.out.printf("Created %d customers for performance testing%n", LARGE_DATASET_SIZE);
    }
}
