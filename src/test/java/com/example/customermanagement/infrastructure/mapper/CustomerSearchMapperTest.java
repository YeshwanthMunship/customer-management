package com.example.customermanagement.infrastructure.mapper;

import com.example.customermanagement.domain.exception.InvalidDateFormatException;
import com.example.customermanagement.domain.model.CustomerSearchCriteria;
import com.example.customermanagement.web.dto.customer.CustomerSearchRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Search Mapper Tests")
class CustomerSearchMapperTest {

    private CustomerSearchMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerSearchMapper();
    }

    @Nested
    @DisplayName("Basic Mapping Tests")
    class BasicMappingTests {

        @Test
        @DisplayName("Should return default criteria for null request DTO")
        void shouldReturnDefaultCriteriaForNullRequestDTO() {
            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria((CustomerSearchRequestDTO) null);

            // Then
            assertNotNull(result);
            assertNull(result.getSearchText());
            assertNull(result.getName());
            assertNull(result.getEmail());
            assertNull(result.getPhone());
            assertNull(result.getCity());
            assertNull(result.getState());
            assertNull(result.getCountry());
            assertNull(result.getZipCode());
            assertNull(result.getCreatedAfter());
            assertNull(result.getCreatedBefore());
            assertNull(result.getUpdatedAfter());
            assertNull(result.getUpdatedBefore());
            assertEquals(0, result.getPage());
            assertEquals(20, result.getSize());
            assertNotNull(result.getSortCriteria());
            assertTrue(result.getSortCriteria().isEmpty());
        }

        @Test
        @DisplayName("Should map all fields from request DTO")
        void shouldMapAllFieldsFromRequestDTO() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSearch("rajesh");
            requestDTO.setName("Rajesh Kumar");
            requestDTO.setEmail("rajesh@example.com");
            requestDTO.setPhone("+91-9876543210");
            requestDTO.setCity("Mumbai");
            requestDTO.setState("Maharashtra");
            requestDTO.setCountry("India");
            requestDTO.setZipCode("400001");
            requestDTO.setCreatedAfterString("2023-01-01T00:00:00");
            requestDTO.setCreatedBeforeString("2023-12-31T23:59:59");
            requestDTO.setUpdatedAfterString("2023-06-01T00:00:00");
            requestDTO.setUpdatedBeforeString("2023-06-30T23:59:59");
            requestDTO.setSort(Arrays.asList("name,asc", "email,desc"));
            requestDTO.setPage(1);
            requestDTO.setSize(50);

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertEquals("rajesh", result.getSearchText());
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("rajesh@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            assertEquals("Mumbai", result.getCity());
            assertEquals("Maharashtra", result.getState());
            assertEquals("India", result.getCountry());
            assertEquals("400001", result.getZipCode());
            assertEquals(LocalDateTime.parse("2023-01-01T00:00:00"), result.getCreatedAfter());
            assertEquals(LocalDateTime.parse("2023-12-31T23:59:59"), result.getCreatedBefore());
            assertEquals(LocalDateTime.parse("2023-06-01T00:00:00"), result.getUpdatedAfter());
            assertEquals(LocalDateTime.parse("2023-06-30T23:59:59"), result.getUpdatedBefore());
            assertEquals(1, result.getPage());
            assertEquals(50, result.getSize());
            assertNotNull(result.getSortCriteria());
            assertEquals(2, result.getSortCriteria().size());
        }

        @Test
        @DisplayName("Should map from individual parameters")
        void shouldMapFromIndividualParameters() {
            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(
                "priya", "Priya Sharma", "priya@example.com", "+91-8765432109",
                "Bangalore", "Karnataka", "India", "560001",
                "2023-01-01T00:00:00", "2023-12-31T23:59:59",
                "2023-06-01T00:00:00", "2023-06-30T23:59:59",
                    List.of("name,asc"), 2, 25
            );

            // Then
            assertEquals("priya", result.getSearchText());
            assertEquals("Priya Sharma", result.getName());
            assertEquals("priya@example.com", result.getEmail());
            assertEquals("+91-8765432109", result.getPhone());
            assertEquals("Bangalore", result.getCity());
            assertEquals("Karnataka", result.getState());
            assertEquals("India", result.getCountry());
            assertEquals("560001", result.getZipCode());
            assertEquals(LocalDateTime.parse("2023-01-01T00:00:00"), result.getCreatedAfter());
            assertEquals(LocalDateTime.parse("2023-12-31T23:59:59"), result.getCreatedBefore());
            assertEquals(LocalDateTime.parse("2023-06-01T00:00:00"), result.getUpdatedAfter());
            assertEquals(LocalDateTime.parse("2023-06-30T23:59:59"), result.getUpdatedBefore());
            assertEquals(2, result.getPage());
            assertEquals(25, result.getSize());
            assertNotNull(result.getSortCriteria());
            assertEquals(1, result.getSortCriteria().size());
        }
    }

    @Nested
    @DisplayName("String Sanitization Tests")
    class StringSanitizationTests {

        @Test
        @DisplayName("Should sanitize strings by trimming whitespace")
        void shouldSanitizeStringsByTrimmingWhitespace() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSearch("  rajesh  ");
            requestDTO.setName("  Rajesh Kumar  ");
            requestDTO.setEmail("  rajesh@example.com  ");
            requestDTO.setPhone("  +91-9876543210  ");
            requestDTO.setCity("  Mumbai  ");
            requestDTO.setState("  Maharashtra  ");
            requestDTO.setCountry("  India  ");
            requestDTO.setZipCode("  400001  ");

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertEquals("rajesh", result.getSearchText());
            assertEquals("Rajesh Kumar", result.getName());
            assertEquals("rajesh@example.com", result.getEmail());
            assertEquals("+91-9876543210", result.getPhone());
            assertEquals("Mumbai", result.getCity());
            assertEquals("Maharashtra", result.getState());
            assertEquals("India", result.getCountry());
            assertEquals("400001", result.getZipCode());
        }

        @Test
        @DisplayName("Should return null for empty or whitespace-only strings")
        void shouldReturnNullForEmptyOrWhitespaceOnlyStrings() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSearch("");
            requestDTO.setName("   ");
            requestDTO.setEmail(null);
            requestDTO.setPhone("\t\n");
            requestDTO.setCity("");
            requestDTO.setState("   ");
            requestDTO.setCountry(null);
            requestDTO.setZipCode("");

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNull(result.getSearchText());
            assertNull(result.getName());
            assertNull(result.getEmail());
            assertNull(result.getPhone());
            assertNull(result.getCity());
            assertNull(result.getState());
            assertNull(result.getCountry());
            assertNull(result.getZipCode());
        }
    }

    @Nested
    @DisplayName("Date Parsing Tests")
    class DateParsingTests {

        @Test
        @DisplayName("Should parse valid ISO 8601 date strings")
        void shouldParseValidISO8601DateStrings() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setCreatedAfterString("2023-10-15T10:30:00");
            requestDTO.setCreatedBeforeString("2023-12-31T23:59:59");
            requestDTO.setUpdatedAfterString("2023-01-01T00:00:00");
            requestDTO.setUpdatedBeforeString("2023-06-30T12:45:30");

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertEquals(LocalDateTime.parse("2023-10-15T10:30:00"), result.getCreatedAfter());
            assertEquals(LocalDateTime.parse("2023-12-31T23:59:59"), result.getCreatedBefore());
            assertEquals(LocalDateTime.parse("2023-01-01T00:00:00"), result.getUpdatedAfter());
            assertEquals(LocalDateTime.parse("2023-06-30T12:45:30"), result.getUpdatedBefore());
        }

        @Test
        @DisplayName("Should return null for null or empty date strings")
        void shouldReturnNullForNullOrEmptyDateStrings() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setCreatedAfterString(null);
            requestDTO.setCreatedBeforeString("");
            requestDTO.setUpdatedAfterString("   ");
            requestDTO.setUpdatedBeforeString("\t\n");

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNull(result.getCreatedAfter());
            assertNull(result.getCreatedBefore());
            assertNull(result.getUpdatedAfter());
            assertNull(result.getUpdatedBefore());
        }

        @Test
        @DisplayName("Should throw exception for invalid date format")
        void shouldThrowExceptionForInvalidDateFormat() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setCreatedAfterString("invalid-date");

            // When & Then
            InvalidDateFormatException exception = assertThrows(
                InvalidDateFormatException.class,
                () -> mapper.toSearchCriteria(requestDTO)
            );
            
            assertTrue(exception.getMessage().contains("Invalid date format"));
            assertTrue(exception.getMessage().contains("invalid-date"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "2023-10-15",           // Missing time
            "2023/10/15 10:30:00",  // Wrong separator
            "15-10-2023T10:30:00",  // Wrong date format
            "2023-13-15T10:30:00",  // Invalid month
            "2023-10-32T10:30:00"   // Invalid day
        })
        @DisplayName("Should throw exception for various invalid date formats")
        void shouldThrowExceptionForVariousInvalidDateFormats(String invalidDate) {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setCreatedAfterString(invalidDate);

            // When & Then
            InvalidDateFormatException exception = assertThrows(
                InvalidDateFormatException.class,
                () -> mapper.toSearchCriteria(requestDTO)
            );
            
            assertTrue(exception.getMessage().contains("Invalid date format"));
            assertTrue(exception.getMessage().contains(invalidDate));
        }
    }

    @Nested
    @DisplayName("Sort Criteria Parsing Tests")
    class SortCriteriaParsingTests {

        @Test
        @DisplayName("Should parse valid sort criteria with direction")
        void shouldParseValidSortCriteriaWithDirection() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("name,asc", "email,desc", "phone,ASC", "city,DESC"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(4, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(1).direction());
            
            assertEquals("phone", result.getSortCriteria().get(2).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(2).direction());
            
            assertEquals("city", result.getSortCriteria().get(3).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(3).direction());
        }

        @Test
        @DisplayName("Should parse sort criteria without direction (defaults to ASC)")
        void shouldParseSortCriteriaWithoutDirectionDefaultsToASC() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("name", "email", "phone"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(3, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(1).direction());
            
            assertEquals("phone", result.getSortCriteria().get(2).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(2).direction());
        }

        @Test
        @DisplayName("Should normalize field names")
        void shouldNormalizeFieldNames() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("zip,asc", "created,desc", "updated,asc", "zipcode,desc"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(4, result.getSortCriteria().size());
            
            assertEquals("zipcode", result.getSortCriteria().get(0).field());
            assertEquals("createdat", result.getSortCriteria().get(1).field());
            assertEquals("updatedat", result.getSortCriteria().get(2).field());
            assertEquals("zipcode", result.getSortCriteria().get(3).field());
        }

        @Test
        @DisplayName("Should ignore invalid sort fields")
        void shouldIgnoreInvalidSortFields() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("name,asc", "invalidField,desc", "email,asc", "anotherInvalid"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(2, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals("email", result.getSortCriteria().get(1).field());
        }

        @Test
        @DisplayName("Should handle null and empty sort list")
        void shouldHandleNullAndEmptySortList() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(null);

            // When
            CustomerSearchCriteria result1 = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result1.getSortCriteria());
            assertTrue(result1.getSortCriteria().isEmpty());

            // Given
            requestDTO.setSort(Collections.emptyList());

            // When
            CustomerSearchCriteria result2 = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result2.getSortCriteria());
            assertTrue(result2.getSortCriteria().isEmpty());
        }

        @Test
        @DisplayName("Should ignore null and empty sort strings")
        void shouldIgnoreNullAndEmptySortStrings() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("name,asc", null, "", "   ", "email,desc"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(2, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals("email", result.getSortCriteria().get(1).field());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "name", "email", "phone", "city", "state", "country", 
            "zipcode", "zip", "createdat", "created", "updatedat", "updated"
        })
        @DisplayName("Should accept all valid sort fields")
        void shouldAcceptAllValidSortFields(String field) {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(List.of(field + ",asc"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(1, result.getSortCriteria().size());
            assertNotNull(result.getSortCriteria().getFirst().field());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalidField", "id", "address", "customerId", "randomField"
        })
        @DisplayName("Should reject invalid sort fields")
        void shouldRejectInvalidSortFields(String field) {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(List.of(field + ",asc"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertTrue(result.getSortCriteria().isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle mixed valid and invalid sort criteria")
        void shouldHandleMixedValidAndInvalidSortCriteria() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList(
                "name,asc",           // Valid
                "invalidField,desc",  // Invalid field
                "email",              // Valid without direction
                "phone,invalid",      // Invalid direction
                "city,desc"           // Valid
            ));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(3, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals("city", result.getSortCriteria().get(2).field());
        }

        @Test
        @DisplayName("Should handle whitespace in sort criteria")
        void shouldHandleWhitespaceInSortCriteria() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("  name  ,  asc  ", "  email  ,  desc  "));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(2, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(1).direction());
        }

        @Test
        @DisplayName("Should handle field-only sort criteria with whitespace")
        void shouldHandleFieldOnlySortCriteriaWithWhitespace() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("  name  ", "  email  ", "  phone  "));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(3, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(1).direction());
            
            assertEquals("phone", result.getSortCriteria().get(2).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(2).direction());
        }

        @Test
        @DisplayName("Should handle mixed format sort criteria with whitespace")
        void shouldHandleMixedFormatSortCriteriaWithWhitespace() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("  name  ,  asc  ", "  email  ", "  phone  ,  desc  "));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(3, result.getSortCriteria().size());
            
            assertEquals("name", result.getSortCriteria().get(0).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            
            assertEquals("email", result.getSortCriteria().get(1).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(1).direction());
            
            assertEquals("phone", result.getSortCriteria().get(2).field());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(2).direction());
        }

        @Test
        @DisplayName("Should handle case insensitive sort directions")
        void shouldHandleCaseInsensitiveSortDirections() {
            // Given
            CustomerSearchRequestDTO requestDTO = new CustomerSearchRequestDTO();
            requestDTO.setSort(Arrays.asList("name,ASC", "email,DESC", "phone,asc", "city,desc"));

            // When
            CustomerSearchCriteria result = mapper.toSearchCriteria(requestDTO);

            // Then
            assertNotNull(result.getSortCriteria());
            assertEquals(4, result.getSortCriteria().size());
            
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(0).direction());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(1).direction());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.ASC, result.getSortCriteria().get(2).direction());
            assertEquals(CustomerSearchCriteria.SortCriteria.SortDirection.DESC, result.getSortCriteria().get(3).direction());
        }
    }
}
