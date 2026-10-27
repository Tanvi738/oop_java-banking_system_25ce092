package com.bank.model;

import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

/**
 * Represents a Current / Checking Account with Overdraft Facility.
 * Demonstrates inheritance and custom polymorphism rules:
 * - Overdraft protection allows balance to become negative up to overdraftLimit.
 * - Flat fee or interest applied when overdraft is tapped.
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_OVERDRAFT_LIMIT = 1000.00;
    public static final double OVERDRAFT_FEE = 15.00;

    private double overdraftLimit;

    public CurrentAccount(String accountNumber, Customer customer, double initialBalance, String pin) {
        super(accountNumber, customer, initialBalance, pin);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public CurrentAccount(String accountNumber, Customer customer, double initialBalance, String pin, double overdraftLimit) {
        super(accountNumber, customer, initialBalance, pin);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "Current / Checking Account";
    }

    @Override
    public synchronized void withdraw(double amount, String description) throws BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: $" + amount);
        }

        // Available funds include current balance + overdraft limit
        double availableFunds = balance + overdraftLimit;

        if (amount > availableFunds) {
            throw new InsufficientFundsException(
                    String.format("Overdraft limit exceeded! Available (Balance + Overdraft Limit of $%.2f): $%.2f",
                            overdraftLimit, availableFunds),
                    amount,
                    availableFunds
            );
        }

        boolean willEnterOverdraft = (balance - amount < 0);
        this.balance -= amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, this.balance, description));

        // If newly entering overdraft, charge overdraft fee
        if (willEnterOverdraft && (this.balance + amount >= 0)) {
            this.balance -= OVERDRAFT_FEE;
            recordTransaction(new Transaction(TransactionType.OVERDRAFT_FEE, OVERDRAFT_FEE, this.balance,
                    "Overdraft Activation Facility Fee"));
        }
    }

    @Override
    public double calculateInterest() {
        // Standard current accounts do not earn interest
        return 0.0;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("==================================================");
        System.out.println("Type:             " + getAccountType());
        System.out.println("Account Number:   " + getAccountNumber());
        System.out.println("Account Holder:   " + getCustomer().getFullName());
        System.out.println("Current Balance:  $" + String.format("%.2f", getBalance()));
        System.out.println("Overdraft Limit:  $" + String.format("%.2f", overdraftLimit));
        System.out.println("Total Available:  $" + String.format("%.2f", getBalance() + overdraftLimit));
        System.out.println("Status:           " + getStatus().getDescription());
        System.out.println("==================================================");
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}
