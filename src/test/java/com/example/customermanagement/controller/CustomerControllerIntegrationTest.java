package com.example.customermanagement.controller;

import com.example.customermanagement.web.dto.address.AddressDTO;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
class CustomerControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Given
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rajesh Kumar"))
                .andExpect(jsonPath("$.email").value("rajesh.kumar@example.com"))
                .andExpect(jsonPath("$.phone").value("+91-9876543210"))
                .andExpect(jsonPath("$.address.street").value("123 MG Road"))
                .andExpect(jsonPath("$.address.city").value("Mumbai"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
    }

    @Test
    void shouldReturnValidationErrorForInvalidCustomer() throws Exception {
        // Given - Invalid customer with missing required fields
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "", "invalid-email", "", null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").exists());
    }

    @Test
    void shouldPatchCustomerSuccessfully() throws Exception {
        // Given - First create a customer
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID from response
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create patch request - update only name and email
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setName("Priya Sharma");
        patchDTO.setEmail("priya.sharma@example.com");

        // When & Then - Patch customer
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.name").value("Priya Sharma"))
                .andExpect(jsonPath("$.email").value("priya.sharma@example.com"))
                .andExpect(jsonPath("$.phone").value("+91-9876543210")) // Should remain unchanged
                .andExpect(jsonPath("$.address.street").value("123 MG Road")) // Should remain unchanged
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldPatchCustomerAddressOnly() throws Exception {
        // Given - First create a customer
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID from response
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create patch request - update only address
        AddressDTO newAddressDTO = new AddressDTO(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
        );
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setAddress(newAddressDTO);

        // When & Then - Patch customer
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.name").value("Rajesh Kumar")) // Should remain unchanged
                .andExpect(jsonPath("$.email").value("rajesh.kumar@example.com")) // Should remain unchanged
                .andExpect(jsonPath("$.phone").value("+91-9876543210")) // Should remain unchanged
                .andExpect(jsonPath("$.address.street").value("456 Brigade Road"))
                .andExpect(jsonPath("$.address.city").value("Bangalore"))
                .andExpect(jsonPath("$.address.state").value("Karnataka"))
                .andExpect(jsonPath("$.address.zipCode").value("560001"))
                .andExpect(jsonPath("$.address.country").value("India"));
    }

    @Test
    void shouldReturnValidationErrorForEmptyPatchRequest() throws Exception {
        // Given - First create a customer
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID from response
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create empty patch request
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();

        // When & Then - Should return error for empty patch
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForNonExistentCustomer() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setName("Priya Sharma");

        // When & Then
        mockMvc.perform(patch("/api/v1/customers/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllCustomersWithoutFilters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetAllCustomersWithPagination() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void shouldGetAllCustomersWithSearchFilter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers")
                        .param("search", "rajesh"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetAllCustomersWithMultipleFilters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers")
                        .param("name", "Rajesh")
                        .param("city", "Mumbai")
                        .param("state", "Maharashtra")
                        .param("sort", "name,asc", "email,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetAllCustomersWithDateFilters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers")
                        .param("createdAfter", "2023-01-01T00:00:00")
                        .param("createdBefore", "2023-12-31T23:59:59")
                        .param("updatedAfter", "2023-06-01T00:00:00")
                        .param("updatedBefore", "2023-06-30T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        // Given - Create a customer first
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // When & Then
        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.name").value("Rajesh Kumar"))
                .andExpect(jsonPath("$.email").value("rajesh.kumar@example.com"))
                .andExpect(jsonPath("$.phone").value("+91-9876543210"))
                .andExpect(jsonPath("$.address.street").value("123 MG Road"))
                .andExpect(jsonPath("$.address.city").value("Mumbai"))
                .andExpect(jsonPath("$.address.state").value("Maharashtra"))
                .andExpect(jsonPath("$.address.zipCode").value("400001"))
                .andExpect(jsonPath("$.address.country").value("India"));
    }

    @Test
    void shouldReturn404WhenGettingNonExistentCustomer() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/v1/customers/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        // Given - Create a customer first
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Update request
        AddressDTO updatedAddressDTO = new AddressDTO(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
        );
        CustomerRequestDTO updateRequestDTO = new CustomerRequestDTO(
                "Rajesh Kumar Updated", "rajesh.updated@example.com", "+91-8765432109", updatedAddressDTO
        );

        // When & Then
        mockMvc.perform(put("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.name").value("Rajesh Kumar Updated"))
                .andExpect(jsonPath("$.email").value("rajesh.updated@example.com"))
                .andExpect(jsonPath("$.phone").value("+91-8765432109"))
                .andExpect(jsonPath("$.address.street").value("456 Brigade Road"))
                .andExpect(jsonPath("$.address.city").value("Bangalore"))
                .andExpect(jsonPath("$.address.state").value("Karnataka"))
                .andExpect(jsonPath("$.address.zipCode").value("560001"))
                .andExpect(jsonPath("$.address.country").value("India"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        // When & Then
        mockMvc.perform(put("/api/v1/customers/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCustomerSuccessfully() throws Exception {
        // Given - Create a customer first
        AddressDTO addressDTO = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", addressDTO
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/" + customerId))
                .andExpect(status().isNoContent());

        // Verify customer is deleted
        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchCustomersWithPagination() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers/search")
                        .param("search", "rajesh")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void shouldSearchCustomersWithAllFilters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers/search")
                        .param("search", "rajesh")
                        .param("name", "Rajesh")
                        .param("email", "rajesh@example.com")
                        .param("phone", "+91-9876543210")
                        .param("city", "Mumbai")
                        .param("state", "Maharashtra")
                        .param("country", "India")
                        .param("zipCode", "400001")
                        .param("createdAfter", "2023-01-01T00:00:00")
                        .param("createdBefore", "2023-12-31T23:59:59")
                        .param("updatedAfter", "2023-06-01T00:00:00")
                        .param("updatedBefore", "2023-06-30T23:59:59")
                        .param("sort", "name,asc", "email,desc")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void shouldSearchCustomersWithDefaultPagination() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers/search")
                        .param("search", "rajesh"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void shouldReturn400WhenCreatingCustomerWithInvalidData() throws Exception {
        // Given - Invalid request with missing required fields
        CustomerRequestDTO invalidRequestDTO = new CustomerRequestDTO();
        invalidRequestDTO.setName(""); // Empty name
        invalidRequestDTO.setEmail("invalid-email"); // Invalid email format
        // Missing phone and address

        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingCustomerWithInvalidData() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerRequestDTO invalidRequestDTO = new CustomerRequestDTO();
        invalidRequestDTO.setName(""); // Empty name
        invalidRequestDTO.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(put("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPatchingCustomerWithInvalidData() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerPatchRequestDTO invalidPatchDTO = new CustomerPatchRequestDTO();
        invalidPatchDTO.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatchDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // with all the Clean Architecture components properly wired together
    }
}