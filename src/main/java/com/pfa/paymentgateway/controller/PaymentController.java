package com.pfa.paymentgateway.controller;

import com.pfa.paymentgateway.dto.PaymentRequest;
import com.pfa.paymentgateway.dto.PaymentResponse;
import com.pfa.paymentgateway.model.Transaction;
import com.pfa.paymentgateway.model.TransactionStatus;
import com.pfa.paymentgateway.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Submit a payment for processing.
     * 
     * @param request Payment request with card details
     * @return Payment response with transaction status
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        logger.info("Received payment request for merchant: {}", request.getMerchantId());

        PaymentResponse response = paymentService.processPayment(request);

        // Return appropriate HTTP status based on transaction status
        HttpStatus httpStatus = getHttpStatus(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Get transaction details by ID.
     * 
     * @param transactionId Transaction identifier
     * @return Transaction details or 404 if not found
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        logger.info("Fetching transaction: {}", transactionId);

        return paymentService.getTransaction(transactionId)
                .map(transaction -> ResponseEntity.ok(mapToDetailedResponse(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }

    private HttpStatus getHttpStatus(TransactionStatus status) {
        return switch (status) {
            case APPROVED -> HttpStatus.OK;
            case DECLINED, FRAUD_SUSPECTED -> HttpStatus.PAYMENT_REQUIRED;
            case THREE_DS_REQUIRED -> HttpStatus.OK;
            case TIMEOUT, ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.OK;
        };
    }

    private Map<String, Object> mapToDetailedResponse(Transaction transaction) {
        return Map.of(
                "transactionId", transaction.getTransactionId(),
                "merchantId", transaction.getMerchantId(),
                "amount", transaction.getAmount(),
                "currency", transaction.getCurrency(),
                "status", transaction.getStatus(),
                "declineCode", transaction.getDeclineCode() != null ? transaction.getDeclineCode() : "",
                "createdAt", transaction.getCreatedAt().toString()
        );
    }
}
