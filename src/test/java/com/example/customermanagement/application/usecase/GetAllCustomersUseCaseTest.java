package com.example.customermanagement.application.usecase;

import com.example.customermanagement.domain.exception.InvalidCustomerDataException;
import com.example.customermanagement.domain.model.Customer;
import com.example.customermanagement.domain.repository.CustomerRepository;
import com.example.customermanagement.infrastructure.mapper.CustomerMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Customers Use Case Tests")
class GetAllCustomersUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private GetAllCustomersUseCase getAllCustomersUseCase;

    private Customer sampleCustomer1;
    private Customer sampleCustomer2;
    private CustomerResponseDTO sampleResponseDTO1;
    private CustomerResponseDTO sampleResponseDTO2;

    @BeforeEach
    void setUp() {
        // Create sample customers with different creation times
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusHours(1);

        sampleCustomer1 = new Customer(
            UUID.randomUUID(),
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            new com.example.customermanagement.domain.model.Address(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
            )
        );
        sampleCustomer1.setCreatedAt(now);

        sampleCustomer2 = new Customer(
            UUID.randomUUID(),
            "Priya Sharma",
            "priya.sharma@example.com",
            "+91-8765432109",
            new com.example.customermanagement.domain.model.Address(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
            )
        );
        sampleCustomer2.setCreatedAt(earlier);

        // Create corresponding response DTOs
        AddressDTO addressDTO1 = new AddressDTO(
            "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        sampleResponseDTO1 = new CustomerResponseDTO(
            sampleCustomer1.getId(),
            "Rajesh Kumar",
            "rajesh.kumar@example.com",
            "+91-9876543210",
            addressDTO1,
            now,
            now
        );

        AddressDTO addressDTO2 = new AddressDTO(
            "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
        );
        sampleResponseDTO2 = new CustomerResponseDTO(
            sampleCustomer2.getId(),
            "Priya Sharma",
            "priya.sharma@example.com",
            "+91-8765432109",
            addressDTO2,
            earlier,
            earlier
        );
    }

    @Nested
    @DisplayName("execute() Tests")
    class ExecuteTests {

        @Test
        @DisplayName("Should return all customers sorted by creation date descending")
        void shouldReturnAllCustomersSortedByCreationDateDescending() {
            // Given
            List<Customer> customers = Arrays.asList(sampleCustomer1, sampleCustomer2);
            List<CustomerResponseDTO> expectedResponse = Arrays.asList(sampleResponseDTO1, sampleResponseDTO2);

            when(customerRepository.findAll()).thenReturn(customers);
            when(customerMapper.toResponseDtoList(customers)).thenReturn(expectedResponse);

            // When
            List<CustomerResponseDTO> result = getAllCustomersUseCase.execute();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponse, result);

            verify(customerRepository).findAll();
            verify(customerMapper).toResponseDtoList(customers);
        }

        @Test
        @DisplayName("Should return empty list when no customers exist")
        void shouldReturnEmptyListWhenNoCustomersExist() {
            // Given
            List<Customer> emptyCustomers = Collections.emptyList();
            List<CustomerResponseDTO> emptyResponse = Collections.emptyList();

            when(customerRepository.findAll()).thenReturn(emptyCustomers);
            when(customerMapper.toResponseDtoList(emptyCustomers)).thenReturn(emptyResponse);

            // When
            List<CustomerResponseDTO> result = getAllCustomersUseCase.execute();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(customerRepository).findAll();
            verify(customerMapper).toResponseDtoList(emptyCustomers);
        }

        @Test
        @DisplayName("Should sort customers by creation date in descending order")
        void shouldSortCustomersByCreationDateInDescendingOrder() {
            // Given - Create customers with specific creation times
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime earlier = now.minusHours(2);
            LocalDateTime latest = now.plusHours(1);

            Customer customer1 = new Customer(
                UUID.randomUUID(),
                "Customer 1",
                "customer1@example.com",
                "+91-1111111111",
                new com.example.customermanagement.domain.model.Address(
                    "Street 1", "City 1", "State 1", "111111", "India"
                )
            );
            customer1.setCreatedAt(earlier);

            Customer customer2 = new Customer(
                UUID.randomUUID(),
                "Customer 2",
                "customer2@example.com",
                "+91-2222222222",
                new com.example.customermanagement.domain.model.Address(
                    "Street 2", "City 2", "State 2", "222222", "India"
                )
            );
            customer2.setCreatedAt(now);

            Customer customer3 = new Customer(
                UUID.randomUUID(),
                "Customer 3",
                "customer3@example.com",
                "+91-3333333333",
                new com.example.customermanagement.domain.model.Address(
                    "Street 3", "City 3", "State 3", "333333", "India"
                )
            );
            customer3.setCreatedAt(latest);

            // Repository returns customers in random order
            List<Customer> customers = Arrays.asList(customer1, customer2, customer3);
            List<CustomerResponseDTO> responseDTOs = Arrays.asList(
                new CustomerResponseDTO(customer1.getId(), "Customer 1", "customer1@example.com", "+91-1111111111", null, earlier, earlier),
                new CustomerResponseDTO(customer2.getId(), "Customer 2", "customer2@example.com", "+91-2222222222", null, now, now),
                new CustomerResponseDTO(customer3.getId(), "Customer 3", "customer3@example.com", "+91-3333333333", null, latest, latest)
            );

            when(customerRepository.findAll()).thenReturn(customers);
            when(customerMapper.toResponseDtoList(any())).thenAnswer(invocation -> {
                List<Customer> sortedCustomers = invocation.getArgument(0);
                // Verify that customers are sorted by creation date descending
                assertEquals(customer3.getId(), sortedCustomers.get(0).getId()); // Latest first
                assertEquals(customer2.getId(), sortedCustomers.get(1).getId()); // Middle
                assertEquals(customer1.getId(), sortedCustomers.get(2).getId()); // Earliest last
                return responseDTOs;
            });

            // When
            List<CustomerResponseDTO> result = getAllCustomersUseCase.execute();

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());

            verify(customerRepository).findAll();
            verify(customerMapper).toResponseDtoList(any());
        }
    }

    @Nested
    @DisplayName("execute(int page, int size) Tests")
    class ExecuteWithPaginationTests {

        @Test
        @DisplayName("Should return paginated customers with valid parameters")
        void shouldReturnPaginatedCustomersWithValidParameters() {
            // Given
            List<Customer> allCustomers = Arrays.asList(sampleCustomer1, sampleCustomer2);
            List<CustomerResponseDTO> responseDTOs = Arrays.asList(sampleResponseDTO1, sampleResponseDTO2);
            long totalCount = 2L;

            when(customerRepository.findAll()).thenReturn(allCustomers);
            when(customerRepository.count()).thenReturn(totalCount);
            when(customerMapper.toResponseDtoList(any())).thenReturn(responseDTOs);

            // When
            PageResponseDTO<CustomerResponseDTO> result = getAllCustomersUseCase.execute(0, 10);

            // Then
            assertNotNull(result);
            assertEquals(responseDTOs, result.getContent());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(totalCount, result.getTotalElements());

            verify(customerRepository).findAll();
            verify(customerRepository).count();
            verify(customerMapper).toResponseDtoList(any());
        }

        @Test
        @DisplayName("Should handle pagination correctly with skip and limit")
        void shouldHandlePaginationCorrectlyWithSkipAndLimit() {
            // Given
            List<Customer> allCustomers = Arrays.asList(sampleCustomer1, sampleCustomer2);
            List<CustomerResponseDTO> responseDTOs = Collections.singletonList(sampleResponseDTO1);

            when(customerRepository.findAll()).thenReturn(allCustomers);
            when(customerRepository.count()).thenReturn(2L);
            when(customerMapper.toResponseDtoList(any())).thenAnswer(invocation -> {
                List<Customer> customers = invocation.getArgument(0);
                // Verify that pagination is applied correctly
                assertEquals(1, customers.size());
                assertEquals(sampleCustomer2.getId(), customers.getFirst().getId()); // Second customer (after skip)
                return responseDTOs;
            });

            // When - Request page 1 with size 1 (should skip first customer)
            PageResponseDTO<CustomerResponseDTO> result = getAllCustomersUseCase.execute(1, 1);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(1, result.getPage());
            assertEquals(1, result.getSize());
            assertEquals(2L, result.getTotalElements());

            verify(customerRepository).findAll();
            verify(customerRepository).count();
            verify(customerMapper).toResponseDtoList(any());
        }

        @Test
        @DisplayName("Should return empty page when page is beyond available data")
        void shouldReturnEmptyPageWhenPageIsBeyondAvailableData() {
            // Given
            List<Customer> allCustomers = Collections.singletonList(sampleCustomer1);
            List<CustomerResponseDTO> emptyResponse = Collections.emptyList();

            when(customerRepository.findAll()).thenReturn(allCustomers);
            when(customerRepository.count()).thenReturn(1L);
            when(customerMapper.toResponseDtoList(any())).thenReturn(emptyResponse);

            // When - Request page 2 with size 10 (beyond available data)
            PageResponseDTO<CustomerResponseDTO> result = getAllCustomersUseCase.execute(2, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(2, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(1L, result.getTotalElements());

            verify(customerRepository).findAll();
            verify(customerRepository).count();
            verify(customerMapper).toResponseDtoList(any());
        }

        @Test
        @DisplayName("Should throw exception when page is negative")
        void shouldThrowExceptionWhenPageIsNegative() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> getAllCustomersUseCase.execute(-1, 10)
            );

            assertEquals("page", exception.getField());
            assertEquals(-1, exception.getValue());
            assertTrue(exception.getMessage().contains("Page number cannot be negative"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should throw exception when size is zero")
        void shouldThrowExceptionWhenSizeIsZero() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> getAllCustomersUseCase.execute(0, 0)
            );

            assertEquals("size", exception.getField());
            assertEquals(0, exception.getValue());
            assertTrue(exception.getMessage().contains("Page size must be greater than 0"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should throw exception when size is negative")
        void shouldThrowExceptionWhenSizeIsNegative() {
            // When & Then
            InvalidCustomerDataException exception = assertThrows(
                InvalidCustomerDataException.class,
                () -> getAllCustomersUseCase.execute(0, -5)
            );

            assertEquals("size", exception.getField());
            assertEquals(-5, exception.getValue());
            assertTrue(exception.getMessage().contains("Page size must be greater than 0"));

            verifyNoInteractions(customerRepository);
            verifyNoInteractions(customerMapper);
        }

        @Test
        @DisplayName("Should handle large page size correctly")
        void shouldHandleLargePageSizeCorrectly() {
            // Given
            List<Customer> allCustomers = Arrays.asList(sampleCustomer1, sampleCustomer2);
            List<CustomerResponseDTO> responseDTOs = Arrays.asList(sampleResponseDTO1, sampleResponseDTO2);

            when(customerRepository.findAll()).thenReturn(allCustomers);
            when(customerRepository.count()).thenReturn(2L);
            when(customerMapper.toResponseDtoList(any())).thenReturn(responseDTOs);

            // When - Request with large page size
            PageResponseDTO<CustomerResponseDTO> result = getAllCustomersUseCase.execute(0, 100);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(0, result.getPage());
            assertEquals(100, result.getSize());
            assertEquals(2L, result.getTotalElements());

            verify(customerRepository).findAll();
            verify(customerRepository).count();
            verify(customerMapper).toResponseDtoList(any());
        }

        @Test
        @DisplayName("Should handle edge case with page 0 and size 1")
        void shouldHandleEdgeCaseWithPage0AndSize1() {
            // Given
            List<Customer> allCustomers = Arrays.asList(sampleCustomer1, sampleCustomer2);
            List<CustomerResponseDTO> responseDTOs = Collections.singletonList(sampleResponseDTO1);

            when(customerRepository.findAll()).thenReturn(allCustomers);
            when(customerRepository.count()).thenReturn(2L);
            when(customerMapper.toResponseDtoList(any())).thenAnswer(invocation -> {
                List<Customer> customers = invocation.getArgument(0);
                assertEquals(1, customers.size());
                assertEquals(sampleCustomer1.getId(), customers.getFirst().getId()); // First customer
                return responseDTOs;
            });

            // When
            PageResponseDTO<CustomerResponseDTO> result = getAllCustomersUseCase.execute(0, 1);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(0, result.getPage());
            assertEquals(1, result.getSize());
            assertEquals(2L, result.getTotalElements());

            verify(customerRepository).findAll();
            verify(customerRepository).count();
            verify(customerMapper).toResponseDtoList(any());
        }
    }
}
