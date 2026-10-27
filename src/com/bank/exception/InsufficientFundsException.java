package com.bank.exception;

/**
 * Thrown when an account does not have adequate funds to complete a withdrawal or transfer.
 */
public class InsufficientFundsException extends BankingException {
    private final double requestedAmount;
    private final double availableBalance;

    public InsufficientFundsException(String message, double requestedAmount, double availableBalance) {
        super(message + String.format(" (Requested: $%.2f, Available: $%.2f)", requestedAmount, availableBalance));
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public InsufficientFundsException(String message) {
        super(message);
        this.requestedAmount = 0.0;
        this.availableBalance = 0.0;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}
