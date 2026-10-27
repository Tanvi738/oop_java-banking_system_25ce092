package com.bank.model;

/**
 * Enumeration representing the operational lifecycle status of a bank account.
 */
public enum AccountStatus {
    ACTIVE("Active"),
    FROZEN("Frozen (Administrative Lock)"),
    DORMANT("Dormant"),
    CLOSED("Closed");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
