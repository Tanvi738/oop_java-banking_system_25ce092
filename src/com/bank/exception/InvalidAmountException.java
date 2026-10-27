package com.bank.exception;

/**
 * Thrown when an invalid transaction amount is specified (e.g. negative or zero).
 */
public class InvalidAmountException extends BankingException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
