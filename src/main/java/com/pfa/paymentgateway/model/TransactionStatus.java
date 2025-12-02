package com.pfa.paymentgateway.model;

/**
 * Enum representing possible transaction statuses.
 */
public enum TransactionStatus {
    PENDING,
    APPROVED,
    DECLINED,
    ERROR,
    TIMEOUT,
    FRAUD_SUSPECTED,
    THREE_DS_REQUIRED
}
