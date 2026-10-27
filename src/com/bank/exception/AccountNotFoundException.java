package com.bank.exception;

/**
 * Thrown when an account number cannot be found in the banking system registry.
 */
public class AccountNotFoundException extends BankingException {
    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
