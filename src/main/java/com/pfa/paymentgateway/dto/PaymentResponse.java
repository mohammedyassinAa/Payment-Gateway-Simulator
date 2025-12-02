package com.pfa.paymentgateway.dto;

import com.pfa.paymentgateway.model.TransactionStatus;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment response DTO.
 */
public class PaymentResponse {

    private String transactionId;
    private TransactionStatus status;
    private String message;
    private String declineCode;
    private BigDecimal amount;
    private String currency;
    private Instant timestamp;
    private String threeDsUrl;

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeclineCode() {
        return declineCode;
    }

    public void setDeclineCode(String declineCode) {
        this.declineCode = declineCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getThreeDsUrl() {
        return threeDsUrl;
    }

    public void setThreeDsUrl(String threeDsUrl) {
        this.threeDsUrl = threeDsUrl;
    }
}
