package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single financial transaction record.
 * Demonstrates immutability and encapsulation.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String description;

    public Transaction(TransactionType type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public Transaction(String transactionId, LocalDateTime timestamp, TransactionType type, double amount, double balanceAfter, String description) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.format(FORMATTER) : "N/A";
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s $%.2f | Balance: $%.2f | %s",
                getTransactionId(),
                getFormattedTimestamp(),
                type.getSign(),
                amount,
                balanceAfter,
                description);
    }
}
