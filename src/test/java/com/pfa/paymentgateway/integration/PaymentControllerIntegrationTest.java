package com.pfa.paymentgateway.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.paymentgateway.dto.PaymentRequest;
import com.pfa.paymentgateway.model.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PaymentController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/payments - approved payment should return 200 OK")
    void processPayment_approved_shouldReturnOk() throws Exception {
        PaymentRequest request = createValidRequest("4242424242424242");

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.message").value("Payment approved"));
    }

    @Test
    @DisplayName("POST /api/payments - declined payment should return 402")
    void processPayment_declined_shouldReturnPaymentRequired() throws Exception {
        PaymentRequest request = createValidRequest("4000000000000002");

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.status").value("DECLINED"))
                .andExpect(jsonPath("$.declineCode").value("card_declined"));
    }

    @Test
    @DisplayName("POST /api/payments - fraud suspected should return 402")
    void processPayment_fraud_shouldReturnPaymentRequired() throws Exception {
        PaymentRequest request = createValidRequest("4000000000000069");

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.status").value("FRAUD_SUSPECTED"))
                .andExpect(jsonPath("$.message", containsString("fraud")));
    }

    @Test
    @DisplayName("POST /api/payments - 3DS required should return 200 with redirect URL")
    void processPayment_3ds_shouldReturnOkWithRedirectUrl() throws Exception {
        PaymentRequest request = createValidRequest("4000000000003220");

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("THREE_DS_REQUIRED"))
                .andExpect(jsonPath("$.threeDsUrl").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/payments - invalid request should return 400")
    void processPayment_invalidRequest_shouldReturnBadRequest() throws Exception {
        PaymentRequest request = new PaymentRequest();
        // Missing required fields

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/payments/{id} - existing transaction should return details")
    void getTransaction_existing_shouldReturnDetails() throws Exception {
        // First, create a transaction
        PaymentRequest request = createValidRequest("4242424242424242");
        MvcResult result = mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String transactionId = objectMapper.readTree(response).get("transactionId").asText();

        // Then, retrieve it
        mockMvc.perform(get("/api/payments/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /api/payments/{id} - non-existing transaction should return 404")
    void getTransaction_nonExisting_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/{id}", "non-existing-id"))
                .andExpect(status().isNotFound());
    }

    private PaymentRequest createValidRequest(String cardNumber) {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("TEST_MERCHANT");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setCardNumber(cardNumber);
        request.setExpiryDate("12/25");
        request.setCvv("123");
        request.setCardholderName("Test User");
        return request;
    }
}
