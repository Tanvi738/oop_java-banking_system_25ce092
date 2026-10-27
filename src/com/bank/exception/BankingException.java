package com.bank.exception;

/**
 * Base exception class for all banking-related business logic errors.
 * Demonstrates inheritance in custom Java exception hierarchies.
 */
public class BankingException extends Exception {
    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
