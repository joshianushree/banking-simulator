import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final String csvFilePath;

    public AccountManager(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        // Ensure CSV header exists
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            // If file is empty, write header. We won't check emptiness — header duplication is okay.
            pw.printf("%s%n", "txId,txType,fromAccount,toAccount,amount,createdAt");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize transactions CSV: " + e.getMessage(), e);
        }
    }

    // createAccount
    public void createAccount(Account account) {
        Objects.requireNonNull(account, "account required");
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists: " + account.getAccountNumber());
        }
        accounts.put(account.getAccountNumber(), account);
    }

    // getAccount
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // listAccounts
    public List<Account> listAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // deposit
    public TransactionRecord deposit(String accountNumber, BigDecimal amount) throws InvalidAmountException {
        Account a = accounts.get(accountNumber);
        if (a == null) throw new IllegalArgumentException("Account not found: " + accountNumber);
        a.deposit(amount);
        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.DEPOSIT, null, accountNumber, amount);
        logTransaction(tx);
        return tx;
    }

    // withdraw
    public TransactionRecord withdraw(String accountNumber, BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        Account a = accounts.get(accountNumber);
        if (a == null) throw new IllegalArgumentException("Account not found: " + accountNumber);
        a.withdraw(amount);
        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.WITHDRAW, accountNumber, null, amount);
        logTransaction(tx);
        return tx;
    }

    // transfer
    public TransactionRecord transfer(String fromAccount, String toAccount, BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }
        // Simple ordering to avoid deadlocks if we later synchronize on accounts
        Account src = accounts.get(fromAccount);
        Account dst = accounts.get(toAccount);
        if (src == null) throw new IllegalArgumentException("Source account not found: " + fromAccount);
        if (dst == null) throw new IllegalArgumentException("Destination account not found: " + toAccount);

        // withdraw then deposit (both operations validate)
        src.withdraw(amount);
        dst.deposit(amount);

        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.TRANSFER, fromAccount, toAccount, amount);
        logTransaction(tx);
        return tx;
    }

    // Log transaction to CSV (append)
    private synchronized void logTransaction(TransactionRecord tx) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            // use TransactionRecord.toString() which formats CSV row
            pw.printf("%s%n", tx.toString());
        } catch (Exception e) {
            // log to console in case of failure
            System.err.println("Failed to write transaction to CSV: " + e.getMessage());
        }
    }
}
