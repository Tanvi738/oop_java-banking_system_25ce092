package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.BankingException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Interface defining primary banking service operations.
 * Demonstrates Abstraction and Interface Segregation in OOP.
 */
public interface BankService {
    Account createSavingsAccount(Customer customer, double initialDeposit, String pin) throws BankingException;
    Account createCurrentAccount(Customer customer, double initialDeposit, String pin, double overdraftLimit) throws BankingException;
    Account createFixedDepositAccount(Customer customer, double initialDeposit, String pin, int termMonths) throws BankingException;
    Account createStudentAccount(Customer customer, double initialDeposit, String pin) throws BankingException;

    Account getAccount(String accountNumber) throws AccountNotFoundException;
    void deposit(String accountNumber, double amount, String description) throws BankingException;
    void withdraw(String accountNumber, double amount, String pin, String description) throws BankingException;
    void transfer(String fromAccountNum, String toAccountNum, double amount, String pin, String description) throws BankingException;

    void freezeAccount(String accountNumber) throws AccountNotFoundException;
    void unfreezeAccount(String accountNumber) throws AccountNotFoundException;
    void closeAccount(String accountNumber) throws AccountNotFoundException;

    double applyMonthlyInterestToAllSavings();
    List<Account> getAllAccounts();
    List<Account> searchAccountsByCustomerName(String nameQuery);
    double getTotalBankDeposits();
    int getTotalAccountCount();
}
