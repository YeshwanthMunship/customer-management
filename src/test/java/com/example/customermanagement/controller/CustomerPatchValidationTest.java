package com.example.customermanagement.controller;

import com.example.customermanagement.web.dto.address.AddressDTO;
import com.example.customermanagement.web.dto.customer.CustomerPatchRequestDTO;
import com.example.customermanagement.web.dto.customer.CustomerRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CustomerPatchValidationTest {

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
    void shouldValidateAddressFieldsInPatchRequest() throws Exception {
        // Given - First create a customer
        AddressDTO validAddress = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequest = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", validAddress
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create patch request with invalid address (empty street)
        AddressDTO invalidAddress = new AddressDTO(
                "", "Bangalore", "Karnataka", "560001", "India"  // Empty street should fail validation
        );
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setAddress(invalidAddress);

        // When & Then - Should return validation error
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").exists());
    }

    @Test
    void shouldValidateAddressWithNullFieldsInPatchRequest() throws Exception {
        // Given - First create a customer
        AddressDTO validAddress = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequest = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", validAddress
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create patch request with invalid address (null city)
        AddressDTO invalidAddress = new AddressDTO(
                "456 Brigade Road", null, "Karnataka", "560001", "India"  // Null city should fail validation
        );
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setAddress(invalidAddress);

        // When & Then - Should return validation error
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").exists());
    }

    @Test
    void shouldAcceptValidAddressInPatchRequest() throws Exception {
        // Given - First create a customer
        AddressDTO validAddress = new AddressDTO(
                "123 MG Road", "Mumbai", "Maharashtra", "400001", "India"
        );
        CustomerRequestDTO createRequest = new CustomerRequestDTO(
                "Rajesh Kumar", "rajesh.kumar@example.com", "+91-9876543210", validAddress
        );

        // Create customer
        MvcResult createResult = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract customer ID
        String responseContent = createResult.getResponse().getContentAsString();
        String customerId = objectMapper.readTree(responseContent).get("id").asText();

        // Create patch request with valid address
        AddressDTO newValidAddress = new AddressDTO(
                "456 Brigade Road", "Bangalore", "Karnataka", "560001", "India"
        );
        CustomerPatchRequestDTO patchDTO = new CustomerPatchRequestDTO();
        patchDTO.setAddress(newValidAddress);

        // When & Then - Should succeed
        mockMvc.perform(patch("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address.street").value("456 Brigade Road"))
                .andExpect(jsonPath("$.address.city").value("Bangalore"));
    }
}
