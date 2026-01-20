package com.pfa.paymentgateway.repository;

import com.pfa.paymentgateway.model.Transaction;

import java.util.Optional;

/**
 * Interface for transaction repository operations.
 * Provides an abstraction layer for transaction persistence.
 */
public interface ITransactionRepository {

    /**
     * Save a transaction.
     * @param transaction the transaction to save
     * @return the saved transaction
     */
    Transaction save(Transaction transaction);

    /**
     * Find a transaction by ID.
     * @param transactionId the transaction ID
     * @return an Optional containing the transaction if found
     */
    Optional<Transaction> findById(String transactionId);

    /**
     * Delete all transactions.
     */
    void deleteAll();

    /**
     * Count all transactions.
     * @return the number of transactions
     */
    int count();
}
