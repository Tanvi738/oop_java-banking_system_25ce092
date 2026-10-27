package com.bank;

import com.bank.exception.BankingException;
import com.bank.model.*;
import com.bank.service.*;
import com.bank.storage.DataPersistenceService;
import com.bank.util.AnsiColor;
import com.bank.util.InputValidator;
import com.bank.util.ReceiptPrinter;

import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for the Object-Oriented Banking System.
 * Developed by: Tanvi Patel (Student ID: 25CE092)
 * Course: Object-Oriented Programming with Java
 */
public class Main {
    private static final String DATA_FILE = "data/bank_state.dat";
    private final BankService bankService;
    private final AuthService authService;
    private final LoanCalculatorService loanService;
    private final Scanner scanner;

    public Main() {
        DataPersistenceService persistenceService = new DataPersistenceService(DATA_FILE);
        this.bankService = new BankServiceImpl(persistenceService);
        this.authService = new AuthService(bankService);
        this.loanService = new LoanCalculatorService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readIntInRange(scanner, "Enter your selection [1-6]: ", 1, 6);
            switch (choice) {
                case 1:
                    handleCustomerLogin();
                    break;
                case 2:
                    handleOpenNewAccount();
                    break;
                case 3:
                    handleAdminPortal();
                    break;
                case 4:
                    handleLoanCalculator();
                    break;
                case 5:
                    displayAboutProject();
                    break;
                case 6:
                    System.out.println(AnsiColor.green("\nThank you for choosing Secure Bank of CHARUSAT. Goodbye!\n"));
                    running = false;
                    break;
            }
        }
    }

    private void printBanner() {
        System.out.println(AnsiColor.cyan("=========================================================================="));
        System.out.println(AnsiColor.bold(AnsiColor.cyan("               BANKING MANAGEMENT SYSTEM - OOP IN JAVA                    ")));
        System.out.println(AnsiColor.cyan("                     Student ID: 25CE092 | Tanvi Patel                    "));
        System.out.println(AnsiColor.cyan("=========================================================================="));
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println(AnsiColor.bold("=========================== MAIN MENU ==========================="));
        System.out.println("  1. Customer Portal (Login with Account Number & PIN)");
        System.out.println("  2. Open New Bank Account");
        System.out.println("  3. Bank Administrator / Manager Portal");
        System.out.println("  4. Loan & EMI Financial Calculator");
        System.out.println("  5. About Project & OOP Concepts Architecture");
        System.out.println("  6. Exit");
        System.out.println("=================================================================");
    }

    // =========================================================================
    // CUSTOMER PORTAL
    // =========================================================================

    private void handleCustomerLogin() {
        System.out.println();
        System.out.println(AnsiColor.bold("--- CUSTOMER AUTHENTICATION ---"));
        String accNum = InputValidator.readNonEmptyString(scanner, "Enter Account Number (e.g., SB-1001): ").toUpperCase();
        String pin = InputValidator.readPin(scanner, "Enter 4-digit Security PIN: ");

        try {
            Account account = authService.authenticateCustomer(accNum, pin);
            System.out.println(AnsiColor.green(" Authentication successful! Welcome, " + account.getCustomer().getFullName() + "!"));
            customerDashboard(account);
        } catch (Exception e) {
            System.out.println(AnsiColor.red(" Authentication Failed: " + e.getMessage()));
        }
    }

    private void customerDashboard(Account account) {
        boolean inSession = true;
        while (inSession) {
            System.out.println();
            System.out.println(AnsiColor.bold("=========== CUSTOMER DASHBOARD (" + account.getAccountNumber() + ") ==========="));
            System.out.println("  1. Check Balance & Account Details");
            System.out.println("  2. Deposit Funds");
            System.out.println("  3. Withdraw Funds");
            System.out.println("  4. Transfer Funds to Another Account");
            System.out.println("  5. View Mini-Statement (Recent 5 Transactions)");
            System.out.println("  6. View Full Statement / Transaction History");
            System.out.println("  7. Change Security PIN");
            System.out.println("  8. Logout to Main Menu");
            System.out.println("=================================================================");

            int choice = InputValidator.readIntInRange(scanner, "Choose action [1-8]: ", 1, 8);
            switch (choice) {
                case 1:
                    account.displayAccountDetails();
                    break;
                case 2:
                    doDeposit(account);
                    break;
                case 3:
                    doWithdraw(account);
                    break;
                case 4:
                    doTransfer(account);
                    break;
                case 5:
                    ReceiptPrinter.printStatement(account, account.getMiniStatement(5));
                    break;
                case 6:
                    ReceiptPrinter.printStatement(account, account.getTransactions());
                    break;
                case 7:
                    doChangePin(account);
                    break;
                case 8:
                    System.out.println(AnsiColor.yellow("Logged out successfully."));
                    inSession = false;
                    break;
            }
        }
    }

    private void doDeposit(Account account) {
        System.out.println();
        System.out.println(AnsiColor.bold("--- DEPOSIT FUNDS ---"));
        double amount = InputValidator.readPositiveDouble(scanner, "Enter amount to deposit ($): ");
        String note = InputValidator.readNonEmptyString(scanner, "Enter deposit remarks/note: ");

        try {
            bankService.deposit(account.getAccountNumber(), amount, note);
            ReceiptPrinter.printTransactionReceipt(
                    "CASH / ONLINE DEPOSIT",
                    account.getAccountNumber(),
                    account.getCustomer().getFullName(),
                    "Deposit",
                    amount,
                    account.getBalance(),
                    note
            );
        } catch (BankingException e) {
            System.out.println(AnsiColor.red(" Deposit Failed: " + e.getMessage()));
        }
    }

    private void doWithdraw(Account account) {
        System.out.println();
        System.out.println(AnsiColor.bold("--- WITHDRAW FUNDS ---"));
        double amount = InputValidator.readPositiveDouble(scanner, "Enter amount to withdraw ($): ");
        String pin = InputValidator.readPin(scanner, "Confirm 4-digit PIN: ");

        try {
            bankService.withdraw(account.getAccountNumber(), amount, pin, "ATM / Counter Cash Withdrawal");
            ReceiptPrinter.printTransactionReceipt(
                    "CASH WITHDRAWAL",
                    account.getAccountNumber(),
                    account.getCustomer().getFullName(),
                    "Withdrawal",
                    amount,
                    account.getBalance(),
                    "Cash Withdrawal"
            );
        } catch (BankingException e) {
            System.out.println(AnsiColor.red(" Withdrawal Failed: " + e.getMessage()));
        }
    }

    private void doTransfer(Account account) {
        System.out.println();
        System.out.println(AnsiColor.bold("--- FUND TRANSFER ---"));
        String targetAcc = InputValidator.readNonEmptyString(scanner, "Enter Destination Account Number: ").toUpperCase();
        double amount = InputValidator.readPositiveDouble(scanner, "Enter Transfer Amount ($): ");
        String note = InputValidator.readNonEmptyString(scanner, "Enter Transfer Purpose / Remarks: ");
        String pin = InputValidator.readPin(scanner, "Enter 4-digit PIN to authorize transfer: ");

        try {
            bankService.transfer(account.getAccountNumber(), targetAcc, amount, pin, note);
            ReceiptPrinter.printTransactionReceipt(
                    "INTER-ACCOUNT FUND TRANSFER",
                    account.getAccountNumber(),
                    account.getCustomer().getFullName(),
                    "Transfer to " + targetAcc,
                    amount,
                    account.getBalance(),
                    note
            );
        } catch (BankingException e) {
            System.out.println(AnsiColor.red(" Transfer Failed: " + e.getMessage()));
        }
    }

    private void doChangePin(Account account) {
        System.out.println();
        System.out.println(AnsiColor.bold("--- CHANGE SECURITY PIN ---"));
        String oldPin = InputValidator.readPin(scanner, "Enter Current PIN: ");
        String newPin = InputValidator.readPin(scanner, "Enter New 4-digit PIN: ");
        String confirmPin = InputValidator.readPin(scanner, "Confirm New 4-digit PIN: ");

        if (!newPin.equals(confirmPin)) {
            System.out.println(AnsiColor.red(" PIN mismatch! New PIN and Confirmation must match."));
            return;
        }

        try {
            account.changePin(oldPin, newPin);
            System.out.println(AnsiColor.green(" Security PIN updated successfully!"));
        } catch (Exception e) {
            System.out.println(AnsiColor.red(" Could not update PIN: " + e.getMessage()));
        }
    }

    // =========================================================================
    // OPEN ACCOUNT
    // =========================================================================

    private void handleOpenNewAccount() {
        System.out.println();
        System.out.println(AnsiColor.bold("--- OPEN A NEW BANK ACCOUNT ---"));
        System.out.println("Select Account Type:");
        System.out.println("  1. Savings Account (4.0% p.a., $500 min balance)");
        System.out.println("  2. Current / Checking Account ($1,000 Overdraft Facility)");
        System.out.println("  3. Fixed Deposit Account (7.5% p.a., Term Lock-in)");
        System.out.println("  4. Student Advantage Account (Zero Balance, $300 daily limit)");
        System.out.println("  5. Cancel");

        int choice = InputValidator.readIntInRange(scanner, "Choose account type [1-5]: ", 1, 5);
        if (choice == 5) return;

        System.out.println("\nCustomer Information:");
        String name = InputValidator.readNonEmptyString(scanner, "Full Legal Name: ");
        String email = InputValidator.readNonEmptyString(scanner, "Email Address: ");
        String phone = InputValidator.readNonEmptyString(scanner, "Phone Number: ");
        String address = InputValidator.readNonEmptyString(scanner, "Residential Address: ");
        String pin = InputValidator.readPin(scanner, "Set a 4-digit PIN: ");

        Customer customer = new Customer("CUST-" + System.currentTimeMillis() % 10000, name, email, phone, address);

        try {
            Account created = null;
            switch (choice) {
                case 1:
                    double initialSav = InputValidator.readPositiveDouble(scanner, "Initial Deposit Amount (Min $" + SavingsAccount.DEFAULT_MIN_BALANCE + "): $");
                    created = bankService.createSavingsAccount(customer, initialSav, pin);
                    break;
                case 2:
                    double initialCurr = InputValidator.readPositiveDouble(scanner, "Initial Deposit Amount: $");
                    created = bankService.createCurrentAccount(customer, initialCurr, pin, CurrentAccount.DEFAULT_OVERDRAFT_LIMIT);
                    break;
                case 3:
                    double initialFd = InputValidator.readPositiveDouble(scanner, "Fixed Deposit Principal (Min $1,000): $");
                    int months = InputValidator.readIntInRange(scanner, "Select Term Tenure (Months) [3-60]: ", 3, 60);
                    created = bankService.createFixedDepositAccount(customer, initialFd, pin, months);
                    break;
                case 4:
                    double initialStud = InputValidator.readPositiveDouble(scanner, "Initial Deposit Amount (Zero Min required): $");
                    created = bankService.createStudentAccount(customer, initialStud, pin);
                    break;
            }

            if (created != null) {
                System.out.println();
                System.out.println(AnsiColor.green(" Account successfully opened!"));
                created.displayAccountDetails();
            }
        } catch (BankingException e) {
            System.out.println(AnsiColor.red(" Account Creation Failed: " + e.getMessage()));
        }
    }

    // =========================================================================
    // ADMIN PORTAL
    // =========================================================================

    private void handleAdminPortal() {
        System.out.println();
        System.out.println(AnsiColor.bold("--- BANK MANAGER / ADMIN PORTAL ---"));
        System.out.println(AnsiColor.yellow("Default credentials: Username = admin | PIN = 9999"));
        String user = InputValidator.readNonEmptyString(scanner, "Admin Username: ");
        String pin = InputValidator.readPin(scanner, "Admin PIN: ");

        try {
            authService.authenticateAdmin(user, pin);
            System.out.println(AnsiColor.green(" Admin Access Granted!"));
            adminDashboard();
        } catch (Exception e) {
            System.out.println(AnsiColor.red(" Admin Login Denied: " + e.getMessage()));
        }
    }

    private void adminDashboard() {
        boolean inSession = true;
        while (inSession) {
            System.out.println();
            System.out.println(AnsiColor.bold("===================== ADMIN DASHBOARD ====================="));
            System.out.println("  1. Bank Overview (Total Accounts & Reserves)");
            System.out.println("  2. List All Registered Accounts");
            System.out.println("  3. Search Accounts by Holder Name or Number");
            System.out.println("  4. Freeze / Lock an Account");
            System.out.println("  5. Unfreeze / Unlock an Account");
            System.out.println("  6. Apply Monthly Interest Batch to All Savings Accounts");
            System.out.println("  7. Close an Account");
            System.out.println("  8. Return to Main Menu");
            System.out.println("===========================================================");

            int choice = InputValidator.readIntInRange(scanner, "Choose admin action [1-8]: ", 1, 8);
            switch (choice) {
                case 1:
                    System.out.println();
                    System.out.println("Total Accounts:    " + bankService.getTotalAccountCount());
                    System.out.printf("Total Bank Reserves: $%,.2f%n", bankService.getTotalBankDeposits());
                    break;
                case 2:
                    displayAllAccounts(bankService.getAllAccounts());
                    break;
                case 3:
                    String query = InputValidator.readNonEmptyString(scanner, "Enter Name or Account # to search: ");
                    List<Account> results = bankService.searchAccountsByCustomerName(query);
                    displayAllAccounts(results);
                    break;
                case 4:
                    String freezeAcc = InputValidator.readNonEmptyString(scanner, "Enter Account Number to FREEZE: ").toUpperCase();
                    try {
                        bankService.freezeAccount(freezeAcc);
                        System.out.println(AnsiColor.yellow(" Account " + freezeAcc + " has been FROZEN."));
                    } catch (Exception e) {
                        System.out.println(AnsiColor.red(" Error: " + e.getMessage()));
                    }
                    break;
                case 5:
                    String unfreezeAcc = InputValidator.readNonEmptyString(scanner, "Enter Account Number to UNFREEZE: ").toUpperCase();
                    try {
                        bankService.unfreezeAccount(unfreezeAcc);
                        System.out.println(AnsiColor.green(" Account " + unfreezeAcc + " has been UNFROZEN and is now ACTIVE."));
                    } catch (Exception e) {
                        System.out.println(AnsiColor.red(" Error: " + e.getMessage()));
                    }
                    break;
                case 6:
                    double totalDisbursed = bankService.applyMonthlyInterestToAllSavings();
                    System.out.printf(AnsiColor.green(" Monthly interest batch executed! Total interest credited: $%,.2f%n"), totalDisbursed);
                    break;
                case 7:
                    String closeAcc = InputValidator.readNonEmptyString(scanner, "Enter Account Number to CLOSE: ").toUpperCase();
                    try {
                        bankService.closeAccount(closeAcc);
                        System.out.println(AnsiColor.yellow(" Account " + closeAcc + " has been permanently CLOSED."));
                    } catch (Exception e) {
                        System.out.println(AnsiColor.red(" Error: " + e.getMessage()));
                    }
                    break;
                case 8:
                    inSession = false;
                    break;
            }
        }
    }

    private void displayAllAccounts(List<Account> list) {
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-12s | %-10s%n", "ACC NO", "HOLDER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println("-----------------------------------------------------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("                            No matching accounts found.                                 ");
        } else {
            for (Account a : list) {
                System.out.printf("%-10s | %-20s | %-15s | $%,11.2f | %-10s%n",
                        a.getAccountNumber(),
                        a.getCustomer().getFullName(),
                        a.getAccountType().length() > 15 ? a.getAccountType().substring(0, 15) : a.getAccountType(),
                        a.getBalance(),
                        a.getStatus());
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // =========================================================================
    // LOAN CALCULATOR
    // =========================================================================

    private void handleLoanCalculator() {
        System.out.println();
        System.out.println(AnsiColor.bold("--- LOAN & EMI FINANCIAL CALCULATOR ---"));
        System.out.println("Select Loan Product:");
        System.out.println("  1. Home Loan (8.50% p.a., up to 30 years)");
        System.out.println("  2. Higher Education Loan (6.80% p.a., up to 10 years)");
        System.out.println("  3. Automobile Car Loan (9.20% p.a., up to 7 years)");
        System.out.println("  4. Personal Unsecured Loan (12.00% p.a., up to 5 years)");
        System.out.println("  5. Return to Main Menu");

        int choice = InputValidator.readIntInRange(scanner, "Choose product [1-5]: ", 1, 5);
        if (choice == 5) return;

        LoanCalculatorService.LoanType selectedType;
        switch (choice) {
            case 1: selectedType = LoanCalculatorService.LoanType.HOME_LOAN; break;
            case 2: selectedType = LoanCalculatorService.LoanType.EDUCATION_LOAN; break;
            case 3: selectedType = LoanCalculatorService.LoanType.CAR_LOAN; break;
            default: selectedType = LoanCalculatorService.LoanType.PERSONAL_LOAN; break;
        }

        double principal = InputValidator.readPositiveDouble(scanner, "Enter Required Loan Amount ($): ");
        int years = InputValidator.readIntInRange(scanner, "Enter Tenure in Years [1-" + selectedType.getMaxTenureYears() + "]: ",
                1, selectedType.getMaxTenureYears());

        LoanCalculatorService.LoanQuote quote = loanService.calculateEmi(selectedType, principal, years * 12);
        quote.printSummary();

        System.out.print("Would you like to check borrowing eligibility based on monthly income? (y/n): ");
        String checkElig = scanner.nextLine().trim();
        if ("y".equalsIgnoreCase(checkElig)) {
            double income = InputValidator.readPositiveDouble(scanner, "Enter Monthly Net Income ($): ");
            boolean eligible = loanService.checkEligibility(income, quote.getMonthlyEmi());
            if (eligible) {
                System.out.println(AnsiColor.green(" APPROVED: Your debt-to-income ratio is healthy! You are eligible for this loan."));
            } else {
                System.out.println(AnsiColor.red(" CAUTION: The required EMI exceeds 40% of your monthly income. Consider a longer tenure or lower loan amount."));
            }
        }
    }

    private void displayAboutProject() {
        System.out.println();
        System.out.println(AnsiColor.cyan("=========================================================================="));
        System.out.println(AnsiColor.bold("               PROJECT OVERVIEW & OOP ARCHITECTURE DESIGN                "));
        System.out.println(AnsiColor.cyan("=========================================================================="));
        System.out.println("Student: Tanvi Patel | Roll No: 25CE092");
        System.out.println("Language: Java (Standard JDK 17 / 21 / 25 compatible)");
        System.out.println();
        System.out.println("KEY OBJECT-ORIENTED PRINCIPLES APPLIED:");
        System.out.println(" 1. ENCAPSULATION:");
        System.out.println("    - Account numbers, PINs, customer credentials, and balances are strictly private.");
        System.out.println("    - State transitions protected via methods with strict validation.");
        System.out.println(" 2. INHERITANCE:");
        System.out.println("    - Base abstract class 'Account' extended by SavingsAccount, CurrentAccount,");
        System.out.println("      FixedDepositAccount, and StudentAccount.");
        System.out.println(" 3. POLYMORPHISM:");
        System.out.println("    - Overridden 'withdraw()', 'calculateInterest()', and 'displayAccountDetails()'.");
        System.out.println("    - Method overloading in 'deposit(amount)' and 'deposit(amount, note)'.");
        System.out.println(" 4. ABSTRACTION & INTERFACES:");
        System.out.println("    - 'BankService' interface decouples business logic from client CLI.");
        System.out.println(" 5. EXCEPTION HANDLING:");
        System.out.println("    - Custom hierarchy: BankingException -> InsufficientFundsException,");
        System.out.println("      AccountNotFoundException, AccountFrozenException, AuthenticationException.");
        System.out.println(" 6. PERSISTENCE & DATA STRUCTURES:");
        System.out.println("    - Map<String, Account> for O(1) indexed lookup.");
        System.out.println("    - DataPersistenceService writes binary serializable snapshots to data/bank_state.dat.");
        System.out.println(AnsiColor.cyan("=========================================================================="));
    }
}
