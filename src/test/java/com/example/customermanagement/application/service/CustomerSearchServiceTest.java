package com.example.customermanagement.application.service;

import com.example.customermanagement.application.usecase.GetAllCustomersUseCase;
import com.example.customermanagement.application.usecase.SearchCustomersUseCase;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.infrastructure.mapper.CustomerSearchMapper;
import com.example.customermanagement.web.dto.customer.CustomerResponseDTO;
import com.example.customermanagement.web.dto.common.PageResponseDTO;
import com.example.customermanagement.web.dto.address.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Search Service Tests")
class CustomerSearchServiceTest {

    @Mock
    private GetAllCustomersUseCase getAllCustomersUseCase;

    @Mock
    private SearchCustomersUseCase searchCustomersUseCase;

    @Mock
    private CustomerSearchMapper customerSearchMapper;

    @InjectMocks
    private CustomerSearchService customerSearchService;

    private CustomerResponseDTO sampleCustomer;
    private PageResponseDTO<CustomerResponseDTO> samplePageResponse;

    @BeforeEach
    void setUp() {
        AddressDTO address = new AddressDTO(
            "123 MG Road",
            "Mumbai",
            "Maharashtra",
            "400001",
            "India"
        );
        
        sampleCustomer = new CustomerResponseDTO(
            UUID.randomUUID(),
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            address,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        samplePageResponse = new PageResponseDTO<>(
                List.of(sampleCustomer),
            0,
            20,
            1L
        );
    }

    @Nested
    @DisplayName("getAllCustomersWithFiltering Tests")
    class GetAllCustomersWithFilteringTests {

        @Test
        @DisplayName("Should return all customers when no filters or pagination provided")
        void shouldReturnAllCustomersWhenNoFiltersOrPaginationProvided() {
            // Given
            List<CustomerResponseDTO> allCustomers = Collections.singletonList(sampleCustomer);
            when(getAllCustomersUseCase.execute()).thenReturn(allCustomers);

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(List.class, response.getBody());
            @SuppressWarnings("unchecked")
            List<CustomerResponseDTO> result = (List<CustomerResponseDTO>) response.getBody();
            assertEquals(1, result.size());
            assertEquals(sampleCustomer, result.getFirst());

            verify(getAllCustomersUseCase).execute();
            verifyNoInteractions(searchCustomersUseCase);
            verifyNoInteractions(customerSearchMapper);
        }

        @Test
        @DisplayName("Should use search with pagination when page and size provided")
        void shouldUseSearchWithPaginationWhenPageAndSizeProvided() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .page(0)
                .size(20)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.executeWithPagination(searchCriteria))
                .thenReturn(samplePageResponse);

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                0, 20, null, null, null, null, null, null, null, null,
                null, null, null, null, null
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(PageResponseDTO.class, response.getBody());
            @SuppressWarnings("unchecked")
            PageResponseDTO<CustomerResponseDTO> result = (PageResponseDTO<CustomerResponseDTO>) response.getBody();
            assertEquals(samplePageResponse, result);

            verify(customerSearchMapper).toSearchCriteria(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, 0, 20
            );
            verify(searchCustomersUseCase).executeWithPagination(searchCriteria);
            verifyNoInteractions(getAllCustomersUseCase);
        }

        @Test
        @DisplayName("Should use search with all results when filters provided but no pagination")
        void shouldUseSearchWithAllResultsWhenFiltersProvidedButNoPagination() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .searchText("rajesh")
                .page(0)
                .size(20)
                .build();
            
            List<CustomerResponseDTO> searchResults = Collections.singletonList(sampleCustomer);
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.executeAllResults(searchCriteria))
                .thenReturn(searchResults);

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                null, null, "rajesh", null, null, null, null, null, null, null,
                null, null, null, null, null
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(List.class, response.getBody());
            @SuppressWarnings("unchecked")
            List<CustomerResponseDTO> result = (List<CustomerResponseDTO>) response.getBody();
            assertEquals(1, result.size());
            assertEquals(sampleCustomer, result.getFirst());

            verify(customerSearchMapper).toSearchCriteria(
                "rajesh", null, null, null, null, null, null, null, null, null,
                null, null, null, 0, 20
            );
            verify(searchCustomersUseCase).executeAllResults(searchCriteria);
            verifyNoInteractions(getAllCustomersUseCase);
        }

        @Test
        @DisplayName("Should use search with pagination when both filters and pagination provided")
        void shouldUseSearchWithPaginationWhenBothFiltersAndPaginationProvided() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .searchText("rajesh")
                .page(1)
                .size(10)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.executeWithPagination(searchCriteria))
                .thenReturn(samplePageResponse);

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                1, 10, "rajesh", null, null, null, null, null, null, null,
                null, null, null, null, null
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(PageResponseDTO.class, response.getBody());

            verify(customerSearchMapper).toSearchCriteria(
                "rajesh", null, null, null, null, null, null, null, null, null,
                null, null, null, 1, 10
            );
            verify(searchCustomersUseCase).executeWithPagination(searchCriteria);
            verifyNoInteractions(getAllCustomersUseCase);
        }

        @Test
        @DisplayName("Should detect filters correctly for various filter types")
        void shouldDetectFiltersCorrectlyForVariousFilterTypes() {
            // Test search filter
            testFilterDetection("search", "rajesh", null, null, null, null, null, null, null, null, null, null, null, null);
            
            // Test name filter
            testFilterDetection("name", null, "Rajesh", null, null, null, null, null, null, null, null, null, null, null);
            
            // Test email filter
            testFilterDetection("email", null, null, "rajesh@example.com", null, null, null, null, null, null, null, null, null, null);
            
            // Test phone filter
            testFilterDetection("phone", null, null, null, "+91-9876543210", null, null, null, null, null, null, null, null, null);
            
            // Test city filter
            testFilterDetection("city", null, null, null, null, "Mumbai", null, null, null, null, null, null, null, null);
            
            // Test state filter
            testFilterDetection("state", null, null, null, null, null, "Maharashtra", null, null, null, null, null, null, null);
            
            // Test country filter
            testFilterDetection("country", null, null, null, null, null, null, "India", null, null, null, null, null, null);
            
            // Test zipCode filter
            testFilterDetection("zipCode", null, null, null, null, null, null, null, "400001", null, null, null, null, null);
            
            // Test createdAfter filter
            testFilterDetection("createdAfter", null, null, null, null, null, null, null, null, "2023-01-01T00:00:00", null, null, null, null);
            
            // Test createdBefore filter
            testFilterDetection("createdBefore", null, null, null, null, null, null, null, null, null, "2023-12-31T23:59:59", null, null, null);
            
            // Test updatedAfter filter
            testFilterDetection("updatedAfter", null, null, null, null, null, null, null, null, null, null, "2023-06-01T00:00:00", null, null);
            
            // Test updatedBefore filter
            testFilterDetection("updatedBefore", null, null, null, null, null, null, null, null, null, null, null, "2023-06-30T23:59:59", null);
            
            // Test sort filter
            testFilterDetection("sort", null, null, null, null, null, null, null, null, null, null, null, null, List.of("name,asc"));
        }

        private void testFilterDetection(String filterType, String search, String name, String email, String phone,
                                       String city, String state, String country, String zipCode, String createdAfter,
                                       String createdBefore, String updatedAfter, String updatedBefore, List<String> sort) {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .page(0)
                .size(20)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.executeAllResults(searchCriteria))
                .thenReturn(Collections.singletonList(sampleCustomer));

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                null, null, search, name, email, phone, city, state, country, zipCode,
                createdAfter, createdBefore, updatedAfter, updatedBefore, sort
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(List.class, response.getBody());

            verify(customerSearchMapper).toSearchCriteria(
                search, name, email, phone, city, state, country, zipCode,
                createdAfter, createdBefore, updatedAfter, updatedBefore,
                sort, 0, 20
            );
            verify(searchCustomersUseCase).executeAllResults(searchCriteria);
            verifyNoInteractions(getAllCustomersUseCase);
        }

        @Test
        @DisplayName("Should use default pagination values when null")
        void shouldUseDefaultPaginationValuesWhenNull() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .page(0)
                .size(20)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.executeAllResults(searchCriteria))
                .thenReturn(Collections.singletonList(sampleCustomer));

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                null, null, "rajesh", null, null, null, null, null, null, null,
                null, null, null, null, null
            );

            // Then
            verify(customerSearchMapper).toSearchCriteria(
                "rajesh", null, null, null, null, null, null, null, null, null,
                null, null, null, 0, 20
            );
            verify(searchCustomersUseCase).executeAllResults(searchCriteria);
        }

        @Test
        @DisplayName("Should handle empty sort list as no filter")
        void shouldHandleEmptySortListAsNoFilter() {
            // Given
            List<CustomerResponseDTO> allCustomers = Collections.singletonList(sampleCustomer);
            when(getAllCustomersUseCase.execute()).thenReturn(allCustomers);

            // When
            ResponseEntity<?> response = customerSearchService.getAllCustomersWithFiltering(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, Collections.emptyList()
            );

            // Then
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertInstanceOf(List.class, response.getBody());

            verify(getAllCustomersUseCase).execute();
            verifyNoInteractions(searchCustomersUseCase);
            verifyNoInteractions(customerSearchMapper);
        }
    }

    @Nested
    @DisplayName("searchCustomers Tests")
    class SearchCustomersTests {

        @Test
        @DisplayName("Should search customers with all parameters")
        void shouldSearchCustomersWithAllParameters() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .searchText("rajesh")
                .name("Rajesh Kumar")
                .email("rajesh@example.com")
                .phone("+91-9876543210")
                .city("Mumbai")
                .state("Maharashtra")
                .country("India")
                .zipCode("400001")
                .createdAfter(LocalDateTime.parse("2023-01-01T00:00:00"))
                .createdBefore(LocalDateTime.parse("2023-12-31T23:59:59"))
                .updatedAfter(LocalDateTime.parse("2023-06-01T00:00:00"))
                .updatedBefore(LocalDateTime.parse("2023-06-30T23:59:59"))
                .page(1)
                .size(10)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.execute(searchCriteria))
                .thenReturn(samplePageResponse);

            // When
            PageResponseDTO<CustomerResponseDTO> result = customerSearchService.searchCustomers(
                "rajesh", "Rajesh Kumar", "rajesh@example.com", "+91-9876543210",
                "Mumbai", "Maharashtra", "India", "400001",
                "2023-01-01T00:00:00", "2023-12-31T23:59:59",
                "2023-06-01T00:00:00", "2023-06-30T23:59:59",
                    List.of("name,asc"), 1, 10
            );

            // Then
            assertNotNull(result);
            assertEquals(samplePageResponse, result);

            verify(customerSearchMapper).toSearchCriteria(
                "rajesh", "Rajesh Kumar", "rajesh@example.com", "+91-9876543210",
                "Mumbai", "Maharashtra", "India", "400001",
                "2023-01-01T00:00:00", "2023-12-31T23:59:59",
                "2023-06-01T00:00:00", "2023-06-30T23:59:59",
                    List.of("name,asc"), 1, 10
            );
            verify(searchCustomersUseCase).execute(searchCriteria);
        }

        @Test
        @DisplayName("Should search customers with minimal parameters")
        void shouldSearchCustomersWithMinimalParameters() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .page(0)
                .size(20)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.execute(searchCriteria))
                .thenReturn(samplePageResponse);

            // When
            PageResponseDTO<CustomerResponseDTO> result = customerSearchService.searchCustomers(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, 0, 20
            );

            // Then
            assertNotNull(result);
            assertEquals(samplePageResponse, result);

            verify(customerSearchMapper).toSearchCriteria(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, 0, 20
            );
            verify(searchCustomersUseCase).execute(searchCriteria);
        }

        @Test
        @DisplayName("Should handle null sort list")
        void shouldHandleNullSortList() {
            // Given
            CustomerSearchCriteria searchCriteria = CustomerSearchCriteria.builder()
                .searchText("rajesh")
                .page(0)
                .size(20)
                .build();
            
            when(customerSearchMapper.toSearchCriteria(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(searchCriteria);
            when(searchCustomersUseCase.execute(searchCriteria))
                .thenReturn(samplePageResponse);

            // When
            PageResponseDTO<CustomerResponseDTO> result = customerSearchService.searchCustomers(
                "rajesh", null, null, null, null, null, null, null,
                null, null, null, null, null, 0, 20
            );

            // Then
            assertNotNull(result);
            assertEquals(samplePageResponse, result);

            verify(customerSearchMapper).toSearchCriteria(
                "rajesh", null, null, null, null, null, null, null,
                null, null, null, null, null, 0, 20
            );
            verify(searchCustomersUseCase).execute(searchCriteria);
        }
    }
}
