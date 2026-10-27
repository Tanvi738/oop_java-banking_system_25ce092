package com.bank.exception;

/**
 * Thrown when an operation is attempted on an account that is frozen or closed.
 */
public class AccountFrozenException extends BankingException {
    public AccountFrozenException(String message) {
        super(message);
    }
}
