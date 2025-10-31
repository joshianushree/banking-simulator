import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a bank account with validated operations for deposit and withdrawal.
 * Supports account types: SAVINGS, CURRENT, STUDENT.
 */
public class Account {
    private String accountNumber;         // 11-digit unique ID (auto-generated if null)
    private final String holderName;
    private final String email;
    private BigDecimal balance;
    private final LocalDateTime createdAt;
    private final String accountType;     // SAVINGS, CURRENT, STUDENT
    private String pin;                   // 4-digit PIN (mutable)
    private String status;                // ACTIVE / LOCKED / CLOSED

    // Full constructor (used by DAO/test)
    public Account(String accountNumber,
                   String holderName,
                   String email,
                   BigDecimal initialBalance,
                   String accountType,
                   String pin) {
        // Auto-generate account number if not provided
        if (accountNumber == null || accountNumber.isBlank()) {
            this.accountNumber = generateAccountNumber();
        } else {
            this.accountNumber = accountNumber;
        }

        this.holderName = Objects.requireNonNull(holderName, "Holder name required");
        this.email = email;

        // Safely set initial balance
        this.balance = (initialBalance == null ? BigDecimal.ZERO : initialBalance)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);

        this.createdAt = LocalDateTime.now();
        this.accountType = (accountType == null || accountType.isBlank())
                ? "SAVINGS"
                : accountType.trim().toUpperCase();

        this.pin = pin;
        this.status = "ACTIVE"; // Default status
    }

    //  Compatibility constructor (older calls)
    public Account(String accountNumber, String holderName, String email, BigDecimal initialBalance) {
        this(accountNumber, holderName, email, initialBalance, "SAVINGS", null);
    }

    //  Generate random 11-digit account number
    private String generateAccountNumber() {
        String uuid = UUID.randomUUID().toString().replaceAll("\\D", "");
        return uuid.substring(0, 11);
    }

    //  Getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public String getEmail() { return email; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getAccountType() { return accountType; }
    public String getPin() { return pin; }
    public String getStatus() { return status; }

    //  Setters (for mutable fields)
    public void setBalance(BigDecimal balance) {
        this.balance = (balance == null ? BigDecimal.ZERO : balance)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) return;
        this.status = status.trim().toUpperCase();
    }

    // Renamed for backward compatibility (some old code may call setPassword)
    public void setPassword(String pin) {
        this.pin = pin;
    }

    //  Verify 4-digit PIN
    public boolean verifyPin(String enteredPin) {
        return this.pin != null && this.pin.equals(enteredPin);
    }

    //  Deposit money (validation included)
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        if (!ValidationUtils.isPositiveAmount(amount)) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        BigDecimal amt = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.balance = this.balance.add(amt);
    }

    //  Withdraw money (with validation and balance check)
    public void withdraw(BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (!ValidationUtils.isPositiveAmount(amount)) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }

        BigDecimal amt = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        if (this.balance.compareTo(amt) < 0) {
            throw new InsufficientFundsException("Insufficient balance.");
        }

        this.balance = this.balance.subtract(amt);
    }

    // ToString for display
    @Override
    public String toString() {
        return String.format(
                "Account[%s] Holder=%s | Type=%s | Status=%s | Balance=%s | CreatedAt=%s",
                accountNumber,
                holderName,
                accountType,
                status,
                balance.toPlainString(),
                createdAt
        );
    }

    public void setCreatedAt(LocalDateTime now) {
    }

    public String getPassword() {
        return "";
    }
}
