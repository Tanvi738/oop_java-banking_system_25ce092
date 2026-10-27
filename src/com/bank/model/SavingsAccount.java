package com.bank.model;

import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

/**
 * Represents a Savings Account.
 * Demonstrates:
 * - Inheritance (extends Account)
 * - Polymorphism (overrides withdraw, calculateInterest, displayAccountDetails)
 * - Encapsulation (maintains minimum balance and interest rules)
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_INTEREST_RATE = 4.0; // 4% per annum
    public static final double DEFAULT_MIN_BALANCE = 500.00; // $500 minimum required

    private double interestRate; // in percentage, e.g. 4.0
    private double minimumBalance;
    private int monthlyWithdrawalCount;
    private static final int MAX_FREE_WITHDRAWALS_PER_MONTH = 5;
    private static final double EXCESS_WITHDRAWAL_FEE = 5.00;

    public SavingsAccount(String accountNumber, Customer customer, double initialBalance, String pin) {
        super(accountNumber, customer, initialBalance, pin);
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.minimumBalance = DEFAULT_MIN_BALANCE;
        this.monthlyWithdrawalCount = 0;
    }

    public SavingsAccount(String accountNumber, Customer customer, double initialBalance, String pin, double interestRate, double minimumBalance) {
        super(accountNumber, customer, initialBalance, pin);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
        this.monthlyWithdrawalCount = 0;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public synchronized void withdraw(double amount, String description) throws BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: $" + amount);
        }

        double fee = 0.0;
        if (monthlyWithdrawalCount >= MAX_FREE_WITHDRAWALS_PER_MONTH) {
            fee = EXCESS_WITHDRAWAL_FEE;
        }

        double totalDeduction = amount + fee;

        if (balance - totalDeduction < minimumBalance) {
            throw new InsufficientFundsException(
                    String.format("Withdrawal rejected. Savings account requires minimum balance of $%.2f. Current balance: $%.2f",
                            minimumBalance, balance),
                    totalDeduction,
                    balance
            );
        }

        this.balance -= amount;
        monthlyWithdrawalCount++;
        recordTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, this.balance, description));

        if (fee > 0) {
            this.balance -= fee;
            recordTransaction(new Transaction(TransactionType.PENALTY, fee, this.balance,
                    "Excess Monthly Withdrawal Fee (Over limit of " + MAX_FREE_WITHDRAWALS_PER_MONTH + ")"));
        }
    }

    @Override
    public double calculateInterest() {
        // Simple monthly accrued interest = (balance * rate / 100) / 12
        return (balance * (interestRate / 100.0)) / 12.0;
    }

    /**
     * Applies monthly accrued interest to the account.
     */
    public synchronized double applyMonthlyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            this.balance += interest;
            recordTransaction(new Transaction(
                    TransactionType.INTEREST_CREDIT,
                    interest,
                    this.balance,
                    String.format("Monthly Interest Credit (@%.2f%% p.a.)", interestRate)
            ));
        }
        return interest;
    }

    public void resetMonthlyWithdrawalCounter() {
        this.monthlyWithdrawalCount = 0;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("==================================================");
        System.out.println("Type:             " + getAccountType());
        System.out.println("Account Number:   " + getAccountNumber());
        System.out.println("Account Holder:   " + getCustomer().getFullName());
        System.out.println("Current Balance:  $" + String.format("%.2f", getBalance()));
        System.out.println("Interest Rate:    " + interestRate + "% p.a.");
        System.out.println("Minimum Balance:  $" + String.format("%.2f", minimumBalance));
        System.out.println("Status:           " + getStatus().getDescription());
        System.out.println("Monthly Txns:     " + monthlyWithdrawalCount + " / " + MAX_FREE_WITHDRAWALS_PER_MONTH + " free withdrawals used");
        System.out.println("==================================================");
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
}
