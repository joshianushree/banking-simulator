import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRecord {
    public enum TxType { DEPOSIT, WITHDRAW, TRANSFER }

    private final String txId;
    private final TxType txType;
    private final String fromAccount; // nullable for deposit
    private final String toAccount;   // nullable for withdraw
    private final BigDecimal amount;
    private final LocalDateTime createdAt;

    public TransactionRecord(TxType txType, String fromAccount, String toAccount, BigDecimal amount) {
        this.txId = UUID.randomUUID().toString();
        this.txType = txType;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.createdAt = LocalDateTime.now();
    }

    public String getTxId() { return txId; }
    public TxType getTxType() { return txType; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s",
                txId, txType, fromAccount == null ? "" : fromAccount,
                toAccount == null ? "" : toAccount, amount.toPlainString(), createdAt.toString());
    }
}
