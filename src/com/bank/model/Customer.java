package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a bank customer/account owner.
 * Demonstrates encapsulation with private fields, validation, and standard accessors.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private final LocalDateTime registeredAt;

    public Customer(String customerId, String fullName, String email, String phoneNumber, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.registeredAt = LocalDateTime.now();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = fullName.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public String getFormattedRegisteredAt() {
        return registeredAt != null ? registeredAt.format(FORMATTER) : "N/A";
    }

    @Override
    public String toString() {
        return String.format("Customer[ID: %s, Name: %s, Email: %s, Phone: %s]",
                customerId, fullName, email, phoneNumber);
    }
}
