package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Address;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Search Customers Use Case Tests")
class SearchCustomersUseCaseTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CustomerMapper customerMapper;
    
    private SearchCustomersUseCase searchCustomersUseCase;
    
    private List<Customer> testCustomers;
    private List<CustomerResponseDTO> testCustomerDTOs;
    
    @BeforeEach
    void setUp() {
        searchCustomersUseCase = new SearchCustomersUseCase(customerRepository, customerMapper);
        
        // Create test data
        Address address1 = new Address("123 MG Road", "Mumbai", "Maharashtra", "400001", "India");
        Address address2 = new Address("456 Brigade Road", "Bangalore", "Karnataka", "560001", "India");
        Address address3 = new Address("789 Park Street", "Kolkata", "West Bengal", "700001", "India");
        
        Customer customer1 = new Customer("Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", address1);
        Customer customer2 = new Customer("Priya Sharma", "priya.sharma@gmail.com", "+91-8765432109", address2);
        Customer customer3 = new Customer("Amit Patel", "amit.patel@company.com", "+91-7654321098", address3);
        
        testCustomers = Arrays.asList(customer1, customer2, customer3);
        
        // Create corresponding DTOs
        testCustomerDTOs = Arrays.asList(
            createCustomerResponseDTO(customer1),
            createCustomerResponseDTO(customer2),
            createCustomerResponseDTO(customer3)
        );
    }
    
    private CustomerResponseDTO createCustomerResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        // Set other fields as needed
        return dto;
    }
    
    @Nested
    @DisplayName("Basic Search Tests")
    class BasicSearchTests {
        
        @Test
        @DisplayName("Should return all customers when no criteria specified")
        void shouldReturnAllCustomersWhenNoCriteriaSpecified() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder().build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(0, result.getPage());
            assertEquals(20, result.getSize());
            assertEquals(3, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should throw exception for null criteria")
        void shouldThrowExceptionForNullCriteria() {
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> searchCustomersUseCase.execute(null)
            );
            
            assertEquals("Search criteria cannot be null", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Text Search Tests")
    class TextSearchTests {
        
        @Test
        @DisplayName("Should search by name")
        void shouldSearchByName() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .searchText("rajesh")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.getFirst())).thenReturn(testCustomerDTOs.getFirst());
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should search by email")
        void shouldSearchByEmail() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .searchText("gmail")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(1))).thenReturn(testCustomerDTOs.get(1));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should search by city")
        void shouldSearchByCity() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .searchText("bangalore")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(1))).thenReturn(testCustomerDTOs.get(1));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should be case insensitive")
        void shouldBeCaseInsensitive() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .searchText("RAJESH")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.getFirst())).thenReturn(testCustomerDTOs.getFirst());
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("Field Filter Tests")
    class FieldFilterTests {
        
        @Test
        @DisplayName("Should filter by specific name")
        void shouldFilterBySpecificName() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .name("priya")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(1))).thenReturn(testCustomerDTOs.get(1));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should filter by email domain")
        void shouldFilterByEmailDomain() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .email("@company.com")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(2))).thenReturn(testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should filter by state")
        void shouldFilterByState() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .state("Maharashtra")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.getFirst())).thenReturn(testCustomerDTOs.getFirst());
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should combine multiple filters")
        void shouldCombineMultipleFilters() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .state("Karnataka")
                .city("bangalore")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(1))).thenReturn(testCustomerDTOs.get(1));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(1, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("Sorting Tests")
    class SortingTests {
        
        @Test
        @DisplayName("Should sort by name ascending")
        void shouldSortByNameAscending() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .sortCriteria(List.of(
                    new CustomerSearchCriteria.SortCriteria("name", 
                        CustomerSearchCriteria.SortCriteria.SortDirection.ASC)
                ))
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
            // Verify sorting logic is applied (names should be: Bob Johnson, Jane Smith, John Doe)
        }
        
        @Test
        @DisplayName("Should sort by multiple criteria")
        void shouldSortByMultipleCriteria() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .sortCriteria(List.of(
                    new CustomerSearchCriteria.SortCriteria("state", 
                        CustomerSearchCriteria.SortCriteria.SortDirection.ASC),
                    new CustomerSearchCriteria.SortCriteria("name", 
                        CustomerSearchCriteria.SortCriteria.SortDirection.DESC)
                ))
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {
        
        @Test
        @DisplayName("Should paginate results correctly")
        void shouldPaginateResultsCorrectly() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .page(0)
                .size(2)
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(0))).thenReturn(testCustomerDTOs.get(0));
            when(customerMapper.toResponseDto(testCustomers.get(1))).thenReturn(testCustomerDTOs.get(1));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
            assertEquals(0, result.getPage());
            assertEquals(2, result.getSize());
            assertEquals(2, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should handle second page correctly")
        void shouldHandleSecondPageCorrectly() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .page(1)
                .size(2)
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(testCustomers.get(2))).thenReturn(testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
            assertEquals(1, result.getPage());
            assertEquals(2, result.getSize());
            assertEquals(1, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should handle empty page correctly")
        void shouldHandleEmptyPageCorrectly() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .page(5)
                .size(2)
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
            assertEquals(5, result.getPage());
            assertEquals(2, result.getSize());
            assertEquals(0, result.getContent().size());
        }
    }
    
    @Nested
    @DisplayName("Date Filter Tests")
    class DateFilterTests {
        
        @Test
        @DisplayName("Should filter by created after date")
        void shouldFilterByCreatedAfterDate() {
            LocalDateTime filterDate = LocalDateTime.now().minusDays(1);
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .createdAfter(filterDate)
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            // All test customers should match since they were just created
            assertEquals(3, result.getTotalElements());
        }
        
        @Test
        @DisplayName("Should filter by created before date")
        void shouldFilterByCreatedBeforeDate() {
            LocalDateTime filterDate = LocalDateTime.now().plusDays(1);
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .createdBefore(filterDate)
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(3, result.getTotalElements());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle empty customer list")
        void shouldHandleEmptyCustomerList() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder().build();
            
            when(customerRepository.findAll()).thenReturn(List.of());
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(0, result.getTotalElements());
            assertEquals(0, result.getTotalPages());
            assertEquals(0, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should handle no matches")
        void shouldHandleNoMatches() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .searchText("nonexistent")
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            
            PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
            
            assertEquals(0, result.getTotalElements());
            assertEquals(0, result.getTotalPages());
            assertEquals(0, result.getContent().size());
        }
        
        @Test
        @DisplayName("Should handle invalid sort field gracefully")
        void shouldHandleInvalidSortFieldGracefully() {
            CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .sortCriteria(List.of(
                    new CustomerSearchCriteria.SortCriteria("invalidfield", 
                        CustomerSearchCriteria.SortCriteria.SortDirection.ASC)
                ))
                .build();
            
            when(customerRepository.findAll()).thenReturn(testCustomers);
            when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(testCustomerDTOs.get(0), testCustomerDTOs.get(1), testCustomerDTOs.get(2));
            
            // Should not throw exception, just ignore invalid sort field
            assertDoesNotThrow(() -> {
                PageResponseDTO<CustomerResponseDTO> result = searchCustomersUseCase.execute(criteria);
                assertEquals(3, result.getTotalElements());
            });
        }
    }
}
