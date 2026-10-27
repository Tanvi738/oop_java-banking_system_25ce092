package com.bank.model;

import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

import java.time.LocalDate;

/**
 * Represents a Fixed Deposit (Term Deposit) Account.
 * Offers higher guaranteed interest for a fixed maturity tenure.
 * Premature withdrawal triggers an early breakage penalty.
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_FD_RATE = 7.5; // 7.5% p.a.
    public static final double PREMATURE_WITHDRAWAL_PENALTY_PERCENT = 1.5; // 1.5% penalty

    private int termMonths;
    private double interestRate;
    private LocalDate maturityDate;
    private boolean isMatured;

    public FixedDepositAccount(String accountNumber, Customer customer, double initialDeposit, String pin, int termMonths) {
        super(accountNumber, customer, initialDeposit, pin);
        this.termMonths = termMonths;
        this.interestRate = DEFAULT_FD_RATE;
        this.maturityDate = LocalDate.now().plusMonths(termMonths);
        this.isMatured = false;
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit Account (" + termMonths + " Months)";
    }

    @Override
    public synchronized void withdraw(double amount, String description) throws BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: $" + amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient FD principal to withdraw.", amount, balance);
        }

        LocalDate today = LocalDate.now();
        boolean premature = today.isBefore(maturityDate);

        double penalty = 0.0;
        if (premature) {
            penalty = amount * (PREMATURE_WITHDRAWAL_PENALTY_PERCENT / 100.0);
        }

        double totalDeduction = amount + penalty;
        if (totalDeduction > balance) {
            throw new InsufficientFundsException("Withdrawal plus premature penalty exceeds balance.", totalDeduction, balance);
        }

        this.balance -= amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, this.balance, description));

        if (penalty > 0) {
            this.balance -= penalty;
            recordTransaction(new Transaction(TransactionType.PENALTY, penalty, this.balance,
                    "Premature FD Breakage Penalty (" + PREMATURE_WITHDRAWAL_PENALTY_PERCENT + "%)"));
        }
    }

    @Override
    public double calculateInterest() {
        // Compound interest on maturity: A = P * (1 + r/n)^(nt)
        // Simple maturity projection:
        return balance * (interestRate / 100.0) * (termMonths / 12.0);
    }

    public double getMaturityAmount() {
        return balance + calculateInterest();
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("==================================================");
        System.out.println("Type:             " + getAccountType());
        System.out.println("Account Number:   " + getAccountNumber());
        System.out.println("Account Holder:   " + getCustomer().getFullName());
        System.out.println("Principal Amount: $" + String.format("%.2f", getBalance()));
        System.out.println("Interest Rate:    " + interestRate + "% p.a.");
        System.out.println("Term Duration:    " + termMonths + " months");
        System.out.println("Maturity Date:    " + maturityDate);
        System.out.println("Est. Return:      $" + String.format("%.2f", getMaturityAmount()));
        System.out.println("Status:           " + getStatus().getDescription());
        System.out.println("==================================================");
    }

    public int getTermMonths() {
        return termMonths;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public double getInterestRate() {
        return interestRate;
    }
}
