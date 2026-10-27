package com.bank.util;

import java.util.Scanner;

/**
 * Robust console input reader with validation and friendly error recovery.
 */
public class InputValidator {

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println(AnsiColor.red(" Input cannot be blank. Please try again."));
        }
    }

    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val > 0) {
                    return val;
                }
                System.out.println(AnsiColor.red(" Amount must be greater than 0."));
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.red(" Invalid number format. Please enter a valid decimal number."));
            }
        }
    }

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf(AnsiColor.red(" Please select an option between %d and %d.%n"), min, max);
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.red(" Please enter a valid integer number."));
            }
        }
    }

    public static String readPin(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String pin = scanner.nextLine().trim();
            if (pin.matches("\\d{4}")) {
                return pin;
            }
            System.out.println(AnsiColor.red(" PIN must consist of exactly 4 numeric digits (e.g. 1234)."));
        }
    }
}
