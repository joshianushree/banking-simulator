import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Account {
    private String accountNumber;
    private String holderName; // will be "Anushree"
    private String email;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    // Constructor
    public Account(String accountNumber, String holderName, String email, BigDecimal initialBalance) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber required");
        this.holderName = Objects.requireNonNull(holderName, "holderName required");
        this.email = email; // optional validation can be added
        if (initialBalance == null) {
            this.balance = BigDecimal.ZERO.setScale(2);
        } else {
            // set scale to 2 for currency-like behaviour
            this.balance = initialBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public String getEmail() { return email; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Deposit method
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        // Ensure same scale and add
        BigDecimal amt = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.balance = this.balance.add(amt);
        // Optionally record createdAt for last operation, but keep it as account creation time per spec
    }

    // Withdraw method
    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        BigDecimal amt = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        if (this.balance.compareTo(amt) < 0) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }
        this.balance = this.balance.subtract(amt);
    }

    @Override
    public String toString() {
        return String.format("Account[%s] holder=%s email=%s balance=%s createdAt=%s",
                accountNumber, holderName, email, balance.toPlainString(), createdAt.toString());
    }
}
