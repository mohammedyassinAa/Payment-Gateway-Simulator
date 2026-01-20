package com.pfa.paymentgateway.repository;

import com.pfa.paymentgateway.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for transactions.
 */
@Repository
public class TransactionRepository implements ITransactionRepository {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    @Override
    public void deleteAll() {
        transactions.clear();
    }

    @Override
    public int count() {
        return transactions.size();
    }
}
