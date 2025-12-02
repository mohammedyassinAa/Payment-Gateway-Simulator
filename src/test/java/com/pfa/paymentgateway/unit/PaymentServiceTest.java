package com.pfa.paymentgateway.unit;

import com.pfa.paymentgateway.dto.PaymentRequest;
import com.pfa.paymentgateway.dto.PaymentResponse;
import com.pfa.paymentgateway.model.Scenario;
import com.pfa.paymentgateway.model.TransactionStatus;
import com.pfa.paymentgateway.repository.ScenarioRepository;
import com.pfa.paymentgateway.repository.TransactionRepository;
import com.pfa.paymentgateway.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentService.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ScenarioRepository scenarioRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(transactionRepository, scenarioRepository);
    }

    @Test
    @DisplayName("Process payment with no matching scenario should return APPROVED")
    void processPayment_noMatchingScenario_shouldApprove() {
        PaymentRequest request = createValidRequest("4242424242424242");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(TransactionStatus.APPROVED, response.getStatus());
        assertEquals("Payment approved", response.getMessage());
        assertNotNull(response.getTransactionId());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Process payment with DECLINED scenario should return DECLINED")
    void processPayment_declinedScenario_shouldDecline() {
        PaymentRequest request = createValidRequest("4000000000000002");
        Scenario declinedScenario = createScenario("declined_card", "DECLINED", "card_declined");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.of(declinedScenario));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(TransactionStatus.DECLINED, response.getStatus());
        assertEquals("card_declined", response.getDeclineCode());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Process payment with FRAUD_SUSPECTED scenario should flag fraud")
    void processPayment_fraudScenario_shouldFlagFraud() {
        PaymentRequest request = createValidRequest("4000000000000069");
        Scenario fraudScenario = createScenario("fraud_card", "FRAUD_SUSPECTED", "fraud_suspected");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.of(fraudScenario));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(TransactionStatus.FRAUD_SUSPECTED, response.getStatus());
        assertTrue(response.getMessage().contains("fraud"));
    }

    @Test
    @DisplayName("Process payment with 3DS scenario should require authentication")
    void processPayment_3dsScenario_shouldRequireAuthentication() {
        PaymentRequest request = createValidRequest("4000000000003220");
        Scenario threeDsScenario = createScenario("3ds_required", "THREE_DS_REQUIRED", "3ds_required");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.of(threeDsScenario));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(TransactionStatus.THREE_DS_REQUIRED, response.getStatus());
        assertNotNull(response.getThreeDsUrl());
        assertTrue(response.getThreeDsUrl().contains("/3ds/authenticate"));
    }

    @Test
    @DisplayName("Process payment with ERROR scenario should return error")
    void processPayment_errorScenario_shouldReturnError() {
        PaymentRequest request = createValidRequest("4000000000000085");
        Scenario errorScenario = createScenario("error_card", "ERROR", "internal_error");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.of(errorScenario));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(TransactionStatus.ERROR, response.getStatus());
        assertTrue(response.getMessage().contains("error"));
    }

    @Test
    @DisplayName("Card fingerprint should not contain actual card number")
    void processPayment_shouldCreateSecureFingerprint() {
        PaymentRequest request = createValidRequest("4242424242424242");
        when(scenarioRepository.findMatchingScenario(anyString(), anyString())).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(i -> {
            var transaction = i.getArguments()[0];
            assertNotNull(transaction);
            return transaction;
        });

        paymentService.processPayment(request);

        verify(transactionRepository).save(argThat(transaction -> {
            String fingerprint = transaction.getCardFingerprint();
            assertNotNull(fingerprint);
            assertFalse(fingerprint.contains("4242"));
            return true;
        }));
    }

    private PaymentRequest createValidRequest(String cardNumber) {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("MERCHANT_001");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setCardNumber(cardNumber);
        request.setExpiryDate("12/25");
        request.setCvv("123");
        return request;
    }

    private Scenario createScenario(String name, String action, String responseCode) {
        Scenario scenario = new Scenario();
        scenario.setName(name);
        scenario.setAction(action);
        scenario.setResponseCode(responseCode);
        scenario.setMatchRules(Map.of("cardNumberPrefix", "4000"));
        return scenario;
    }
}
