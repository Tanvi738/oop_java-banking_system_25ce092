package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.AuthenticationException;
import com.bank.model.Account;

/**
 * Service managing user and administrative authentication.
 */
public class AuthService {
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PIN = "9999";

    private final BankService bankService;

    public AuthService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Authenticates a customer by account number and 4-digit PIN.
     */
    public Account authenticateCustomer(String accountNumber, String pin) throws AuthenticationException, AccountNotFoundException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new AuthenticationException("Account number cannot be empty.");
        }
        if (pin == null || pin.trim().isEmpty()) {
            throw new AuthenticationException("PIN cannot be empty.");
        }

        Account account = bankService.getAccount(accountNumber.trim());
        if (!account.validatePin(pin.trim())) {
            throw new AuthenticationException("Invalid security PIN for account " + accountNumber);
        }

        return account;
    }

    /**
     * Authenticates bank manager / admin credentials.
     */
    public boolean authenticateAdmin(String username, String pin) throws AuthenticationException {
        if (DEFAULT_ADMIN_USERNAME.equalsIgnoreCase(username) && DEFAULT_ADMIN_PIN.equals(pin)) {
            return true;
        }
        throw new AuthenticationException("Invalid administrator credentials.");
    }
}
