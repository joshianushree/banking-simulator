# Banking Simulator

## Task 2 – Account Model

### Overview
This module defines the `Account` class with deposit and withdrawal features using OOP principles.

### Files Implemented
- `Account.java`
- `InvalidAmountException.java`
- `InsufficientFundsException.java`
- `MainTest.java`

### Functionality
- Deposit and Withdraw methods include validation for negative or zero amounts.
- Withdrawals cannot exceed available balance.
- Each account has account number, holder name, email, balance, and creation time.

### Why BigDecimal for Money
We use `java.math.BigDecimal` to represent monetary values because:
- Floating-point (`double`/`float`) types can introduce rounding errors.
- `BigDecimal` allows precise decimal arithmetic and scale control.
- It ensures accuracy for all currency operations (2 decimal places, HALF_EVEN rounding mode).
