package com.bank.storage;

import com.bank.model.Account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object (DTO) container for bank state persistence.
 */
public class BankData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Account> accounts;
    private int accountCounter;

    public BankData() {
        this.accounts = new HashMap<>();
        this.accountCounter = 1000;
    }

    public BankData(Map<String, Account> accounts, int accountCounter) {
        this.accounts = accounts;
        this.accountCounter = accountCounter;
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public int getAccountCounter() {
        return accountCounter;
    }

    public void setAccountCounter(int accountCounter) {
        this.accountCounter = accountCounter;
    }
}
