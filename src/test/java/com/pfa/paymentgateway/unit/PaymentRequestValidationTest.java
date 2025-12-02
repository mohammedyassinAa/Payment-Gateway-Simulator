package com.pfa.paymentgateway.unit;

import com.pfa.paymentgateway.dto.PaymentRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentRequest DTO validation.
 */
class PaymentRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid payment request should pass validation")
    void validPaymentRequest_shouldPassValidation() {
        PaymentRequest request = createValidRequest();
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    @DisplayName("Missing merchant ID should fail validation")
    void missingMerchantId_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setMerchantId(null);
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("merchantId")));
    }

    @Test
    @DisplayName("Invalid card number should fail validation")
    void invalidCardNumber_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setCardNumber("123"); // Too short
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cardNumber")));
    }

    @Test
    @DisplayName("Invalid expiry date format should fail validation")
    void invalidExpiryDate_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setExpiryDate("2025-12"); // Wrong format
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expiryDate")));
    }

    @Test
    @DisplayName("Invalid CVV should fail validation")
    void invalidCvv_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setCvv("12"); // Too short
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cvv")));
    }

    @Test
    @DisplayName("Amount less than 0.01 should fail validation")
    void amountTooLow_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setAmount(new BigDecimal("0.001"));
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    @DisplayName("Invalid currency code should fail validation")
    void invalidCurrencyCode_shouldFailValidation() {
        PaymentRequest request = createValidRequest();
        request.setCurrency("EURO"); // Should be 3 chars
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currency")));
    }

    private PaymentRequest createValidRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("MERCHANT_001");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setCardNumber("4242424242424242");
        request.setExpiryDate("12/25");
        request.setCvv("123");
        request.setCardholderName("John Doe");
        return request;
    }
}
