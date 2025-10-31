import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class TransactionRecord {

    //  Enum for limited transaction types
    public enum TxType {
        DEPOSIT, WITHDRAW, TRANSFER, ACCOUNT_CLOSED
    }

    //  Fields
    private final String txId;
    private final TxType txType;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String category; // Optional (e.g., "Salary", "Bills", etc.)
    private LocalDateTime createdAt; // ⬅️ Removed 'final' so DB can set exact timestamp

    //  Constructor (most common)
    public TransactionRecord(TxType txType, String fromAccount, String toAccount, BigDecimal amount) {
        this(txType, fromAccount, toAccount, amount, null);
    }

    //  Full constructor (with category)
    public TransactionRecord(TxType txType, String fromAccount, String toAccount, BigDecimal amount, String category) {
        this.txId = UUID.randomUUID().toString();
        this.txType = Objects.requireNonNull(txType, "Transaction type cannot be null");
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = validateAmount(amount);
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category.trim();
        this.createdAt = LocalDateTime.now();
    }

    // Validate transaction amount
    private BigDecimal validateAmount(BigDecimal amt) {
        if (amt == null || amt.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Transaction amount must be non-negative");
        return amt.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    //  Getters
    public String getTxId() { return txId; }
    public TxType getTxType() { return txType; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    //  Setter only for DAO (database loading)
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //  Date formatting helper
    public String getFormattedDate() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return createdAt.format(fmt);
    }

    //  ToString for display/logging
    @Override
    public String toString() {
        return String.format(
                "%s | %-10s | ₹%-10s | From: %-8s | To: %-8s | Category: %-10s | Date: %s",
                txId.substring(0, 8),
                txType,
                amount,
                fromAccount == null ? "-" : fromAccount,
                toAccount == null ? "-" : toAccount,
                category,
                getFormattedDate()
        );
    }
}
