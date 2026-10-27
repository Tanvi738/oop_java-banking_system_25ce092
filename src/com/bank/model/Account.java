package com.bank.model;

import com.bank.exception.AccountFrozenException;
import com.bank.exception.AuthenticationException;
import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class representing a generic Bank Account.
 * Core foundation demonstrating:
 * 1. Abstraction (abstract withdraw, calculateInterest, getAccountType)
 * 2. Encapsulation (protected/private fields, controlled access via methods)
 * 3. Polymorphism (derived subclasses override behavior)
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String accountNumber;
    private final Customer customer;
    protected double balance;
    private String pin; // 4-digit security PIN
    private AccountStatus status;
    private final LocalDateTime createdAt;
    protected final List<Transaction> transactions;

    public Account(String accountNumber, Customer customer, double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = Math.max(0.0, initialBalance);
        this.pin = pin;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();

        if (initialBalance > 0) {
            this.transactions.add(new Transaction(
                    TransactionType.DEPOSIT,
                    initialBalance,
                    this.balance,
                    "Initial Opening Deposit"
            ));
        }
    }

    // --- Abstract Methods (Must be implemented by subclasses) ---
    public abstract String getAccountType();
    public abstract void withdraw(double amount, String description) throws BankingException;
    public abstract double calculateInterest();
    public abstract void displayAccountDetails();

    // --- Concrete Methods ---

    /**
     * Deposits money into the account.
     * Method overloading example (with description).
     */
    public synchronized void deposit(double amount, String description) throws BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly greater than 0. Provided: $" + amount);
        }

        this.balance += amount;
        Transaction txn = new Transaction(TransactionType.DEPOSIT, amount, this.balance, description);
        this.transactions.add(txn);
    }

    /**
     * Overloaded deposit without custom description.
     */
    public void deposit(double amount) throws BankingException {
        deposit(amount, "Standard Cash/Online Deposit");
    }

    /**
     * Validates account status before executing operations.
     */
    protected void ensureActive() throws AccountFrozenException {
        if (status == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account " + accountNumber + " is FROZEN. Please contact bank administration.");
        }
        if (status == AccountStatus.CLOSED) {
            throw new AccountFrozenException("Account " + accountNumber + " is CLOSED. No transactions permitted.");
        }
    }

    /**
     * Validates PIN securely.
     */
    public boolean validatePin(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    /**
     * Changes security PIN after verifying the old PIN.
     */
    public void changePin(String oldPin, String newPin) throws AuthenticationException {
        if (!validatePin(oldPin)) {
            throw new AuthenticationException("Incorrect existing PIN.");
        }
        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new AuthenticationException("New PIN must be exactly 4 numeric digits.");
        }
        this.pin = newPin;
    }

    /**
     * Appends an external transaction (e.g. interest credit, transfer).
     */
    public synchronized void recordTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    // --- Getters and Setters ---

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.format(FORMATTER) : "N/A";
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Transaction> getMiniStatement(int count) {
        int size = transactions.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(transactions.subList(fromIndex, size));
    }
}
