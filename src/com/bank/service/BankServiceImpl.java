package com.bank.service;

import com.bank.exception.*;
import com.bank.model.*;
import com.bank.storage.BankData;
import com.bank.storage.DataPersistenceService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Core implementation of BankService managing account lifecycles,
 * thread-safe transactions, atomic fund transfers, and data persistence.
 */
public class BankServiceImpl implements BankService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicInteger accountCounter = new AtomicInteger(1001);
    private final DataPersistenceService persistenceService;

    public BankServiceImpl(DataPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        loadOrInitialize();
    }

    private void loadOrInitialize() {
        if (persistenceService != null) {
            BankData data = persistenceService.loadData();
            if (data != null && data.getAccounts() != null && !data.getAccounts().isEmpty()) {
                this.accounts.putAll(data.getAccounts());
                this.accountCounter.set(Math.max(1001, data.getAccountCounter()));
                return;
            }
        }
        // Seed default starter accounts for demo/testing convenience
        seedStarterData();
    }

    private void seedStarterData() {
        try {
            Customer c1 = new Customer("CUST-101", "Tanvi Patel", "tanvi@charusat.edu.in", "+91-9876543210", "Anand, Gujarat");
            Customer c2 = new Customer("CUST-102", "Alex Mercer", "alex.mercer@gmail.com", "+1-555-0199", "New York, NY");
            Customer c3 = new Customer("CUST-103", "Priya Sharma", "priya.s@techcorp.com", "+91-9123456789", "Ahmedabad, Gujarat");

            createSavingsAccount(c1, 1500.00, "1234");
            createCurrentAccount(c2, 3500.00, "4321", 1500.00);
            createStudentAccount(c3, 250.00, "1111");
            createFixedDepositAccount(c1, 5000.00, "1234", 12);
        } catch (BankingException e) {
            System.err.println("Notice initializing starter accounts: " + e.getMessage());
        }
    }

    private void persist() {
        if (persistenceService != null) {
            persistenceService.saveData(new BankData(new HashMap<>(accounts), accountCounter.get()));
        }
    }

    private String generateAccountNumber(String prefix) {
        return prefix + accountCounter.getAndIncrement();
    }

    @Override
    public synchronized Account createSavingsAccount(Customer customer, double initialDeposit, String pin) throws BankingException {
        if (customer == null) throw new BankingException("Customer information is required.");
        if (pin == null || !pin.matches("\\d{4}")) throw new AuthenticationException("PIN must be 4 numeric digits.");
        if (initialDeposit < SavingsAccount.DEFAULT_MIN_BALANCE) {
            throw new InvalidAmountException("Initial deposit for Savings Account must be at least $" + SavingsAccount.DEFAULT_MIN_BALANCE);
        }

        String accNum = generateAccountNumber("SB-");
        SavingsAccount account = new SavingsAccount(accNum, customer, initialDeposit, pin);
        accounts.put(accNum, account);
        persist();
        return account;
    }

    @Override
    public synchronized Account createCurrentAccount(Customer customer, double initialDeposit, String pin, double overdraftLimit) throws BankingException {
        if (customer == null) throw new BankingException("Customer information is required.");
        if (pin == null || !pin.matches("\\d{4}")) throw new AuthenticationException("PIN must be 4 numeric digits.");
        if (initialDeposit < 0) throw new InvalidAmountException("Initial deposit cannot be negative.");

        String accNum = generateAccountNumber("CA-");
        CurrentAccount account = new CurrentAccount(accNum, customer, initialDeposit, pin, Math.max(500.0, overdraftLimit));
        accounts.put(accNum, account);
        persist();
        return account;
    }

    @Override
    public synchronized Account createFixedDepositAccount(Customer customer, double initialDeposit, String pin, int termMonths) throws BankingException {
        if (customer == null) throw new BankingException("Customer information is required.");
        if (pin == null || !pin.matches("\\d{4}")) throw new AuthenticationException("PIN must be 4 numeric digits.");
        if (initialDeposit < 1000.00) throw new InvalidAmountException("Fixed deposit minimum amount is $1,000.00");
        if (termMonths < 3) throw new InvalidAmountException("Minimum fixed deposit tenure is 3 months.");

        String accNum = generateAccountNumber("FD-");
        FixedDepositAccount account = new FixedDepositAccount(accNum, customer, initialDeposit, pin, termMonths);
        accounts.put(accNum, account);
        persist();
        return account;
    }

    @Override
    public synchronized Account createStudentAccount(Customer customer, double initialDeposit, String pin) throws BankingException {
        if (customer == null) throw new BankingException("Customer information is required.");
        if (pin == null || !pin.matches("\\d{4}")) throw new AuthenticationException("PIN must be 4 numeric digits.");
        if (initialDeposit < 0) throw new InvalidAmountException("Initial deposit cannot be negative.");

        String accNum = generateAccountNumber("ST-");
        StudentAccount account = new StudentAccount(accNum, customer, initialDeposit, pin);
        accounts.put(accNum, account);
        persist();
        return account;
    }

    @Override
    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null) throw new AccountNotFoundException("null");
        Account acc = accounts.get(accountNumber.trim().toUpperCase());
        if (acc == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return acc;
    }

    @Override
    public synchronized void deposit(String accountNumber, double amount, String description) throws BankingException {
        Account account = getAccount(accountNumber);
        account.deposit(amount, description);
        persist();
    }

    @Override
    public synchronized void withdraw(String accountNumber, double amount, String pin, String description) throws BankingException {
        Account account = getAccount(accountNumber);
        if (!account.validatePin(pin)) {
            throw new AuthenticationException("Security PIN verification failed for account " + accountNumber);
        }
        account.withdraw(amount, description);
        persist();
    }

    @Override
    public synchronized void transfer(String fromAccountNum, String toAccountNum, double amount, String pin, String description) throws BankingException {
        if (fromAccountNum == null || toAccountNum == null) {
            throw new BankingException("Both source and destination account numbers must be specified.");
        }
        if (fromAccountNum.trim().equalsIgnoreCase(toAccountNum.trim())) {
            throw new BankingException("Self-transfer is not permitted. Source and destination accounts are identical.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be strictly greater than $0.00");
        }

        Account source = getAccount(fromAccountNum);
        Account destination = getAccount(toAccountNum);

        if (!source.validatePin(pin)) {
            throw new AuthenticationException("Security PIN verification failed for source account " + fromAccountNum);
        }

        if (source.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Source account " + fromAccountNum + " is not ACTIVE (" + source.getStatus() + ").");
        }
        if (destination.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Destination account " + toAccountNum + " is not ACTIVE (" + destination.getStatus() + ").");
        }

        // Atomic transaction: withdraw from source first
        source.withdraw(amount, "Transfer to " + destination.getAccountNumber() + " (" + destination.getCustomer().getFullName() + ")");

        // Deposit into destination
        try {
            destination.deposit(amount, "Transfer from " + source.getAccountNumber() + " (" + source.getCustomer().getFullName() + ") - " + description);
        } catch (Exception e) {
            // Rollback in case of unexpected deposit failure
            source.deposit(amount, "Rollback: Failed transfer to " + destination.getAccountNumber());
            persist();
            throw new BankingException("Transfer failed during credit phase: " + e.getMessage() + ". Rolled back.", e);
        }

        persist();
    }

    @Override
    public synchronized void freezeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.setStatus(AccountStatus.FROZEN);
        persist();
    }

    @Override
    public synchronized void unfreezeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        persist();
    }

    @Override
    public synchronized void closeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        persist();
    }

    @Override
    public synchronized double applyMonthlyInterestToAllSavings() {
        double totalDisbursed = 0.0;
        for (Account acc : accounts.values()) {
            if (acc instanceof SavingsAccount && acc.getStatus() == AccountStatus.ACTIVE) {
                totalDisbursed += ((SavingsAccount) acc).applyMonthlyInterest();
            }
        }
        persist();
        return totalDisbursed;
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public List<Account> searchAccountsByCustomerName(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String q = nameQuery.trim().toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getCustomer().getFullName().toLowerCase().contains(q) ||
                             a.getAccountNumber().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalBankDeposits() {
        return accounts.values().stream().mapToDouble(Account::getBalance).sum();
    }

    @Override
    public int getTotalAccountCount() {
        return accounts.size();
    }
}
