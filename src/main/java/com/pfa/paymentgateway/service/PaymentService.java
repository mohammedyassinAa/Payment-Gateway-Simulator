package com.pfa.paymentgateway.service;

import com.pfa.paymentgateway.dto.PaymentRequest;
import com.pfa.paymentgateway.dto.PaymentResponse;
import com.pfa.paymentgateway.model.Scenario;
import com.pfa.paymentgateway.model.Transaction;
import com.pfa.paymentgateway.model.TransactionStatus;
import com.pfa.paymentgateway.repository.IScenarioRepository;
import com.pfa.paymentgateway.repository.ITransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Service for processing payments with simulation logic.
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final ITransactionRepository transactionRepository;
    private final IScenarioRepository scenarioRepository;

    public PaymentService(ITransactionRepository transactionRepository, IScenarioRepository scenarioRepository) {
        this.transactionRepository = transactionRepository;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Process a payment request and return simulated response.
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for merchant: {}", request.getMerchantId());

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setCardFingerprint(createCardFingerprint(request.getCardNumber()));
        transaction.setMetadata(request.getMetadata());
        transaction.setStatus(TransactionStatus.PENDING);

        // Find matching scenario
        Optional<Scenario> matchingScenario = scenarioRepository
                .findMatchingScenario(request.getCardNumber(), request.getMerchantId());

        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setTimestamp(Instant.now());

        if (matchingScenario.isPresent()) {
            Scenario scenario = matchingScenario.get();
            logger.info("Matched scenario: {}", scenario.getName());
            applyScenario(transaction, response, scenario);
        } else {
            // Default: Approve the transaction
            logger.info("No matching scenario, approving transaction");
            transaction.setStatus(TransactionStatus.APPROVED);
            response.setStatus(TransactionStatus.APPROVED);
            response.setMessage("Payment approved");
        }

        // Save transaction
        transactionRepository.save(transaction);

        return response;
    }

    /**
     * Apply scenario rules to the transaction and response.
     */
    private void applyScenario(Transaction transaction, PaymentResponse response, Scenario scenario) {
        // Apply delay if configured
        if (scenario.getDelayMs() != null && scenario.getDelayMs() > 0) {
            try {
                logger.info("Applying delay of {} ms", scenario.getDelayMs());
                Thread.sleep(scenario.getDelayMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Sleep interrupted", e);
            }
        }

        // Set status based on action
        TransactionStatus status;
        String message;
        try {
            status = TransactionStatus.valueOf(scenario.getAction());
        } catch (IllegalArgumentException e) {
            status = TransactionStatus.ERROR;
        }

        transaction.setStatus(status);
        transaction.setDeclineCode(scenario.getResponseCode());
        response.setStatus(status);
        response.setDeclineCode(scenario.getResponseCode());

        switch (status) {
            case APPROVED:
                message = "Payment approved";
                break;
            case DECLINED:
                message = "Payment declined: " + scenario.getResponseCode();
                break;
            case FRAUD_SUSPECTED:
                message = "Payment flagged for fraud review";
                break;
            case TIMEOUT:
                message = "Payment processing timeout";
                break;
            case THREE_DS_REQUIRED:
                message = "3D Secure authentication required";
                response.setThreeDsUrl("/3ds/authenticate?transactionId=" + transaction.getTransactionId());
                break;
            case ERROR:
            default:
                message = "Payment processing error";
                break;
        }
        response.setMessage(message);
    }

    /**
     * Retrieve transaction by ID.
     */
    public Optional<Transaction> getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    /**
     * Create a card fingerprint (hash) for secure storage.
     * Never store actual card numbers.
     */
    private String createCardFingerprint(String cardNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cardNumber.getBytes());
            return HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to create card fingerprint", e);
            return "fingerprint_error";
        }
    }
}
