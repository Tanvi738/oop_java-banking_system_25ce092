package com.bank.model;

/**
 * Enumeration representing all types of banking transactions.
 */
public enum TransactionType {
    DEPOSIT("Deposit", "+"),
    WITHDRAWAL("Withdrawal", "-"),
    TRANSFER_OUT("Transfer Sent", "-"),
    TRANSFER_IN("Transfer Received", "+"),
    INTEREST_CREDIT("Interest Credit", "+"),
    OVERDRAFT_FEE("Overdraft Fee", "-"),
    PENALTY("Penalty Fee", "-");

    private final String displayName;
    private final String sign;

    TransactionType(String displayName, String sign) {
        this.displayName = displayName;
        this.sign = sign;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSign() {
        return sign;
    }
}
