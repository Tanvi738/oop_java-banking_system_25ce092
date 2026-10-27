package com.bank.model;

import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

/**
 * Represents a Student Account.
 * Tailored for students:
 * - Zero minimum balance requirement.
 * - Daily withdrawal safety limit.
 * - Modest interest rate (1.5% p.a.).
 */
public class StudentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_DAILY_LIMIT = 300.00;
    private double dailyWithdrawalLimit;
    private double withdrawnToday;
    private double interestRate = 1.5;

    public StudentAccount(String accountNumber, Customer customer, double initialBalance, String pin) {
        super(accountNumber, customer, initialBalance, pin);
        this.dailyWithdrawalLimit = DEFAULT_DAILY_LIMIT;
        this.withdrawnToday = 0.0;
    }

    @Override
    public String getAccountType() {
        return "Student Advantage Account (Zero-Balance)";
    }

    @Override
    public synchronized void withdraw(double amount, String description) throws BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: $" + amount);
        }

        if (withdrawnToday + amount > dailyWithdrawalLimit) {
            throw new BankingException(String.format(
                    "Daily withdrawal limit of $%.2f exceeded! Withdrawn today: $%.2f, Remaining allowance: $%.2f",
                    dailyWithdrawalLimit, withdrawnToday, Math.max(0, dailyWithdrawalLimit - withdrawnToday)));
        }

        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient balance in student account.", amount, balance);
        }

        this.balance -= amount;
        this.withdrawnToday += amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, this.balance, description));
    }

    @Override
    public double calculateInterest() {
        return (balance * (interestRate / 100.0)) / 12.0;
    }

    public void resetDailyCounter() {
        this.withdrawnToday = 0.0;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("==================================================");
        System.out.println("Type:             " + getAccountType());
        System.out.println("Account Number:   " + getAccountNumber());
        System.out.println("Account Holder:   " + getCustomer().getFullName());
        System.out.println("Current Balance:  $" + String.format("%.2f", getBalance()));
        System.out.println("Min Balance Req:  $0.00 (Zero Minimum Balance)");
        System.out.println("Daily Limit:      $" + String.format("%.2f", dailyWithdrawalLimit));
        System.out.println("Withdrawn Today:  $" + String.format("%.2f", withdrawnToday));
        System.out.println("Status:           " + getStatus().getDescription());
        System.out.println("==================================================");
    }

    public double getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(double dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }
}
