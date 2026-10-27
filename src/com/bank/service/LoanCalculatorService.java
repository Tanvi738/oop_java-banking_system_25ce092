package com.bank.service;

/**
 * Extra Feature: Loan & EMI Calculation Service.
 * Provides financial computation for loan products, monthly installments (EMI),
 * and borrowing eligibility assessment.
 */
public class LoanCalculatorService {

    public enum LoanType {
        HOME_LOAN("Home Mortgage Loan", 8.50, 30),
        EDUCATION_LOAN("Higher Education Loan", 6.80, 10),
        CAR_LOAN("Automobile Loan", 9.20, 7),
        PERSONAL_LOAN("Personal Unsecured Loan", 12.00, 5);

        private final String title;
        private final double interestRate;
        private final int maxTenureYears;

        LoanType(String title, double interestRate, int maxTenureYears) {
            this.title = title;
            this.interestRate = interestRate;
            this.maxTenureYears = maxTenureYears;
        }

        public String getTitle() {
            return title;
        }

        public double getInterestRate() {
            return interestRate;
        }

        public int getMaxTenureYears() {
            return maxTenureYears;
        }
    }

    public static class LoanQuote {
        private final LoanType loanType;
        private final double principal;
        private final double annualRate;
        private final int tenureMonths;
        private final double monthlyEmi;
        private final double totalInterest;
        private final double totalAmount;

        public LoanQuote(LoanType loanType, double principal, double annualRate, int tenureMonths,
                          double monthlyEmi, double totalInterest, double totalAmount) {
            this.loanType = loanType;
            this.principal = principal;
            this.annualRate = annualRate;
            this.tenureMonths = tenureMonths;
            this.monthlyEmi = monthlyEmi;
            this.totalInterest = totalInterest;
            this.totalAmount = totalAmount;
        }

        public void printSummary() {
            System.out.println("==================================================");
            System.out.println("            LOAN QUOTE & EMI BREAKDOWN            ");
            System.out.println("==================================================");
            System.out.println("Product:           " + loanType.getTitle());
            System.out.printf("Principal Amount:  $%,.2f%n", principal);
            System.out.printf("Interest Rate:     %.2f%% p.a.%n", annualRate);
            System.out.println("Tenure:            " + tenureMonths + " months (" + (tenureMonths / 12) + " years)");
            System.out.printf("Monthly EMI:       $%,.2f%n", monthlyEmi);
            System.out.printf("Total Interest:    $%,.2f%n", totalInterest);
            System.out.printf("Total Repayment:   $%,.2f%n", totalAmount);
            System.out.println("==================================================");
        }

        public double getMonthlyEmi() { return monthlyEmi; }
        public double getTotalInterest() { return totalInterest; }
        public double getTotalAmount() { return totalAmount; }
    }

    /**
     * Calculates EMI using the standard reducing balance loan formula:
     * EMI = [P * r * (1+r)^n] / [(1+r)^n - 1]
     */
    public LoanQuote calculateEmi(LoanType loanType, double principal, int tenureMonths) {
        double monthlyRate = (loanType.getInterestRate() / 100.0) / 12.0;
        double factor = Math.pow(1.0 + monthlyRate, tenureMonths);
        double emi = (principal * monthlyRate * factor) / (factor - 1.0);
        double totalRepayment = emi * tenureMonths;
        double totalInterest = totalRepayment - principal;

        return new LoanQuote(loanType, principal, loanType.getInterestRate(), tenureMonths, emi, totalInterest, totalRepayment);
    }

    /**
     * Checks borrowing eligibility based on debt-to-income ratio (max 40% of income for EMI).
     */
    public boolean checkEligibility(double monthlyIncome, double estimatedEmi) {
        double maxAllowableEmi = monthlyIncome * 0.40;
        return estimatedEmi <= maxAllowableEmi;
    }
}
