# Banking Management System — Object-Oriented Programming in Java

[![Java Version](https://img.shields.io/badge/Java-17%2B%20%7C%2021%2B-ED8B00?logo=openjdk&logoColor=white)](#prerequisites)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen)](#quick-start)
[![Architecture](https://img.shields.io/badge/Architecture-OOP%20Design%20Patterns-blue)](#key-oop-concepts-demonstrated)
[![Author](https://img.shields.io/badge/Author-Tanvi%20Patel%20%2825CE092%29-orange)](#author--academic-context)

> An enterprise-grade, interactive **Banking Management System** developed in **Core Java** demonstrating fundamental and advanced **Object-Oriented Programming (OOP)** paradigms. Includes multi-account hierarchy, secure authentication, atomic fund transfers, administrator operations, loan EMI calculator, transaction audit logging, and file persistence.

---

##  Quick Clone & Run

Anyone can clone and run this repository directly from the terminal with **zero external dependencies**:

```bash
# 1. Clone the repository
git clone https://github.com/Tanvi738/oop_java-banking_system_25ce092.git

# 2. Enter project directory
cd oop_java-banking_system_25ce092

# 3. Build and launch (macOS / Linux)
./run.sh
```

> **Windows Users:** Run `scripts\run.bat` or run:
> ```cmd
> javac -d bin -sourcepath src src/com/bank/Main.java
> java -cp bin com.bank.Main
> ```

---

##  Table of Contents
- [Quick Clone & Run](#-quick-clone--run)
- [Project Overview](#-project-overview)
- [Key OOP Concepts Demonstrated](#-key-oop-concepts-demonstrated)
- [Features & Capabilities](#-features--capabilities)
- [Account Hierarchy](#-account-hierarchy)
- [Project Structure](#-project-structure)
- [Preloaded Demo Accounts](#-preloaded-demo-accounts)
- [Author & Academic Context](#-author--academic-context)

---

## 💡 Project Overview

This project simulates a complete commercial retail bank core processing system. Designed with clean code architecture and modular separation of concerns, the system is divided into domain models, services, storage layer, utilities, and an interactive console terminal UI.

```
       +-------------------------------------------------------+
       |                  Presentation Layer                   |
       |             (com.bank.Main Console UI)                |
       +-------------------------------------------------------+
                                   |
         +-------------------------+-------------------------+
         |                                                   |
         v                                                   v
+-------------------+                               +--------------------+
|  Customer Portal  |                               |    Admin Portal    |
+-------------------+                               +--------------------+
         |                                                   |
         +-------------------------+-------------------------+
                                   v
       +-------------------------------------------------------+
       |                     Service Layer                     |
       |  (BankService, AuthService, LoanCalculatorService)    |
       +-------------------------------------------------------+
                                   |
                                   v
       +-------------------------------------------------------+
       |                      Model Layer                      |
       |   (Account -> Savings, Current, FD, StudentAccount)   |
       +-------------------------------------------------------+
                                   |
                                   v
       +-------------------------------------------------------+
       |                    Storage Layer                      |
       |       (DataPersistenceService -> data/bank_state.dat) |
       +-------------------------------------------------------+
```

---

## 🧠 Key OOP Concepts Demonstrated

### 1. Encapsulation
- All state fields (`accountNumber`, `balance`, `pin`, `transactions`) are marked `private` or `protected`.
- Sensitive operations like PIN verification and balance changes are governed by strict mutators and validation rules.
- Transaction records are immutable value objects (`Transaction.java`).

### 2. Inheritance
- Base abstract class [`Account`](src/com/bank/model/Account.java) defines common state and contracts.
- Specialized classes inherit and extend base capabilities:
  - [`SavingsAccount`](src/com/bank/model/SavingsAccount.java): Minimum balance requirement, interest compounding, monthly withdrawal quotas.
  - [`CurrentAccount`](src/com/bank/model/CurrentAccount.java): Overdraft facility allowing negative balances up to approved limits.
  - [`FixedDepositAccount`](src/com/bank/model/FixedDepositAccount.java): Fixed term maturity, lock-in period, premature withdrawal penalty.
  - [`StudentAccount`](src/com/bank/model/StudentAccount.java): Zero-balance account with daily spending safety caps.

### 3. Polymorphism
- **Dynamic Method Overriding (`@Override`)**: Subclasses provide distinct implementations of:
  - `withdraw(double amount, String description)`
  - `calculateInterest()`
  - `displayAccountDetails()`
- **Static Method Overloading**: Multiple overloaded variants for deposits and transactions:
  - `deposit(double amount)`
  - `deposit(double amount, String description)`

### 4. Abstraction & Interfaces
- [`BankService`](src/com/bank/service/BankService.java) interface provides a decoupled contract for banking operations, shielding client code from underlying implementation details.

### 5. Custom Exception Hierarchy
Comprehensive business exception handling extending `BankingException`:
- `InsufficientFundsException` — Detailed debit rejections with requested vs available balance.
- `AccountNotFoundException` — Missing or invalid account lookups.
- `AccountFrozenException` — Operations attempted on locked or closed accounts.
- `InvalidAmountException` — Non-positive, zero, or corrupt monetary inputs.
- `AuthenticationException` — PIN/credentials verification failures.

### 6. Collections Framework & Concurrency
- `Map<String, Account>` using `ConcurrentHashMap` for $O(1)$ constant-time account lookups.
- Generic `List<Transaction>` for passbook audit logging.
- `AtomicInteger` for thread-safe sequential account number generation.

---

## ✨ Features & Capabilities

### 👤 Customer Operations
- **Account Login:** Secure login with Account Number and 4-digit PIN.
- **Real-Time Balance Check:** Detailed inspection of balances, overdraft headroom, or maturity yield.
- **Cash Deposit:** Instant credit with remarks and automated transaction logging.
- **Cash Withdrawal:** Validates balance, account type rules, overdrafts, and minimum reserves.
- **Inter-Account Fund Transfer:** Atomic transfer with automatic rollback if either debit or credit fails.
- **Mini-Statement & Passbook:** Generates formatted statements displaying transaction ID, timestamp, debit/credit sign, and running balance.
- **PIN Management:** Self-service PIN modification with verification.
- **Digital Advice Receipts:** Formatted ASCII printed slips for every transaction.

### 🛡️ Bank Administrator Portal
- **Executive Overview:** Real-time visibility into total active accounts and cumulative bank deposits/liquidity.
- **Full Account Registry:** Tabular listing of all accounts, types, holders, and balances.
- **Account Search:** Fast search by customer name or account number.
- **Fraud Protection & Risk Controls:** Freeze/unfreeze suspicious accounts instantly.
- **Batch Interest Disbursement:** Automated monthly interest processing across all eligible savings accounts in a single execution.
- **Account Closure:** Administrative decommissioning of accounts.

### 📊 Extra Feature: Loan & EMI Financial Calculator
- **Product Coverage:**
  - Home Loan (8.50% p.a., up to 30 years)
  - Higher Education Loan (6.80% p.a., up to 10 years)
  - Automobile Loan (9.20% p.a., up to 7 years)
  - Personal Loan (12.00% p.a., up to 5 years)
- **Financial Formulation:** Standard reducing balance monthly installment formula:
  $$\text{EMI} = \frac{P \times r \times (1+r)^n}{(1+r)^n - 1}$$
- **Borrowing Eligibility Estimator:** Evaluates customer debt-to-income ratio (DTI $\le 40\%$).

### 💾 Extra Feature: File-Based Persistence
- Automatically persists account states, balances, and transaction journals to `data/bank_state.dat`.
- Preserves customer data seamlessly between application restarts.

---

## 🏛️ Account Hierarchy

| Account Type | Prefix | Min. Balance | Interest Rate | Overdraft Facility | Special Rules |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Savings Account** | `SB-` | \$500.00 | 4.00% p.a. | ❌ No | Max 5 free monthly withdrawals |
| **Current Account** | `CA-` | \$0.00 | 0.00% | ✅ Up to \$1,000 | Overdraft activation fee applies |
| **Fixed Deposit** | `FD-` | \$1,000.00 | 7.50% p.a. | ❌ No | Lock-in term; premature penalty |
| **Student Account** | `ST-` | \$0.00 | 1.50% p.a. | ❌ No | \$300.00 daily safety cap |

---

## 📂 Project Structure

```
oop_java-banking_system_25ce092/
├── src/
│   └── com/
│       └── bank/
│           ├── Main.java                         # Application entrypoint & CLI Controller
│           ├── model/
│           │   ├── Account.java                  # Abstract base account class
│           │   ├── SavingsAccount.java           # Savings account implementation
│           │   ├── CurrentAccount.java           # Checking/current account with overdraft
│           │   ├── FixedDepositAccount.java      # Fixed deposit term account
│           │   ├── StudentAccount.java           # Zero-balance student account
│           │   ├── Customer.java                 # Customer profile domain model
│           │   ├── Transaction.java              # Transaction audit log record
│           │   ├── TransactionType.java          # Transaction types enum
│           │   └── AccountStatus.java            # Account status lifecycle enum
│           ├── service/
│           │   ├── BankService.java              # Banking operations interface
│           │   ├── BankServiceImpl.java          # Bank service business logic
│           │   ├── AuthService.java              # Customer & admin authentication
│           │   └── LoanCalculatorService.java    # Loan EMI & eligibility service
│           ├── storage/
│           │   ├── BankData.java                 # State DTO
│           │   └── DataPersistenceService.java   # File serialization persistence
│           ├── exception/
│           │   ├── BankingException.java         # Base banking exception
│           │   ├── InsufficientFundsException.java
│           │   ├── AccountNotFoundException.java
│           │   ├── AccountFrozenException.java
│           │   ├── InvalidAmountException.java
│           │   └── AuthenticationException.java
│           └── util/
│               ├── AnsiColor.java                # Terminal color highlights
│               ├── InputValidator.java           # Robust CLI input handler
│               └── ReceiptPrinter.java           # Formatted ASCII receipt generator
├── data/
│   └── bank_state.dat                            # Binary persistence storage (auto-created)
├── scripts/
│   ├── run.sh                                    # macOS / Linux runner
│   └── run.bat                                   # Windows runner
├── run.sh                                        # Root one-click runner
├── pom.xml                                       # Maven build descriptor
├── .gitignore                                    # Git exclusion rules
└── README.md                                     # Project documentation
```

---

## 🔑 Preloaded Demo Accounts

The system seeds sample accounts on first run so evaluators can test immediately:

| Role / Account | Account Number | Security PIN | Initial Balance | Details |
| :--- | :--- | :--- | :--- | :--- |
| **Savings Account** | `SB-1001` | `1234` | \$1,500.00 | Holder: Tanvi Patel |
| **Current Account** | `CA-1002` | `4321` | \$3,500.00 | Holder: Alex Mercer (\$1,500 Overdraft) |
| **Student Account** | `ST-1003` | `1111` | \$250.00 | Holder: Priya Sharma (Zero min balance) |
| **Fixed Deposit** | `FD-1004` | `1234` | \$5,000.00 | Holder: Tanvi Patel (12 Months @ 7.5%) |
| **Bank Administrator** | `admin` | `9999` | — | Full managerial privileges |

---

## 🛠️ Build and Compilation Options

### Option A: Direct Bash Script (Simplest)
```bash
./run.sh
```

### Option B: Standard `javac` & `java`
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin com.bank.Main
```

### Option C: Apache Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.bank.Main"
```

---

## 👩‍💻 Author & Academic Context

- **Student Name:** Tanvi Patel
- **Student ID:** 25CE092
- **Subject:** Object-Oriented Programming with Java
- **GitHub Repository:** [https://github.com/Tanvi738/oop_java-banking_system_25ce092.git](https://github.com/Tanvi738/oop_java-banking_system_25ce092.git)

---

⭐ *If you find this project helpful or educational, feel free to star the repository!*
