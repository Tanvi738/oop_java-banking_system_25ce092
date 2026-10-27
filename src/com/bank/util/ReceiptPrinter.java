package com.bank.util;

import com.bank.model.Account;
import com.bank.model.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility for rendering ASCII receipts and account mini-statements.
 */
public class ReceiptPrinter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void printTransactionReceipt(String title, String accountNum, String holderName,
                                               String type, double amount, double currentBalance, String reference) {
        System.out.println();
        System.out.println("=============================================================");
        System.out.println("                  SECURE BANK OF CHARUSAT                   ");
        System.out.println("                 TRANSACTION ADVICE RECEIPT                 ");
        System.out.println("=============================================================");
        System.out.println(" Date & Time:     " + LocalDateTime.now().format(FORMATTER));
        System.out.println(" Receipt Title:   " + title);
        System.out.println(" Account Number:  " + accountNum);
        System.out.println(" Account Holder:  " + holderName);
        System.out.println(" Transaction:     " + type);
        System.out.printf(" Amount:          $%,.2f%n", amount);
        System.out.printf(" Updated Balance: $%,.2f%n", currentBalance);
        System.out.println(" Reference / Note:" + reference);
        System.out.println(" Status:          SUCCESSFUL (COMPLETED)");
        System.out.println("=============================================================");
        System.out.println(" Thank you for banking with us! Keep this receipt for records.");
        System.out.println("=============================================================");
        System.out.println();
    }

    public static void printStatement(Account account, List<Transaction> transactions) {
        System.out.println();
        System.out.println("==========================================================================================");
        System.out.println("                                  ACCOUNT STATEMENT                                       ");
        System.out.println("==========================================================================================");
        System.out.println("Account Number: " + account.getAccountNumber() + " | Type: " + account.getAccountType());
        System.out.println("Account Holder: " + account.getCustomer().getFullName() + " | Email: " + account.getCustomer().getEmail());
        System.out.printf("Current Balance: $%,.2f | Status: %s%n", account.getBalance(), account.getStatus());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-14s | %-19s | %-18s | %-12s | %-12s | %-15s%n",
                "TXN ID", "DATE & TIME", "TYPE", "AMOUNT", "BALANCE", "DESCRIPTION");
        System.out.println("------------------------------------------------------------------------------------------");

        if (transactions.isEmpty()) {
            System.out.println("                             No transactions recorded yet.                               ");
        } else {
            for (Transaction t : transactions) {
                String amountStr = String.format("%s$%,.2f", t.getType().getSign(), t.getAmount());
                String balanceStr = String.format("$%,.2f", t.getBalanceAfter());
                System.out.printf("%-14s | %-19s | %-18s | %-12s | %-12s | %-15s%n",
                        t.getTransactionId(),
                        t.getFormattedTimestamp(),
                        t.getType().getDisplayName(),
                        amountStr,
                        balanceStr,
                        t.getDescription());
            }
        }
        System.out.println("==========================================================================================");
        System.out.println();
    }
}
