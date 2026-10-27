package com.bank.exception;

/**
 * Thrown when credentials (PIN, admin key) fail authentication checks.
 */
public class AuthenticationException extends BankingException {
    public AuthenticationException(String message) {
        super(message);
    }
}
