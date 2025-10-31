import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AccountManager {

    private final AccountDao accountDao;
    private final TransactionDao txDao;

    //  Constructor initializes DAOs
    public AccountManager() {
        this.accountDao = new AccountDao();
        this.txDao = new TransactionDao();
    }

    //  Create account (used in BankingApp)
    public void createAccount(Account a) {
        accountDao.createAccount(a);
    }

    //  Overloaded version (optional, used for other setups)
    public void createAccount(String accNo, String name, String email, String password, BigDecimal balance, String status) {
        Account acc = new Account(accNo, name, email, balance);
        acc.setPassword(password);
        acc.setStatus(status);
        accountDao.createAccount(acc);
    }

    //  Deposit logic
    public void deposit(String accNo, BigDecimal amount) {
        if (!ValidationUtils.isPositiveAmount(amount))
            throw new IllegalArgumentException("Invalid deposit amount");

        Account a = accountDao.findByAccountNumber(accNo);
        if (a == null)
            throw new IllegalArgumentException("Account not found: " + accNo);

        a.setBalance(a.getBalance().add(amount));
        accountDao.updateBalanceAndActivity(a);

        // Log deposit
        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.DEPOSIT, null, accNo, amount);
        txDao.saveTransaction(tx);
    }

    //  Withdraw logic (with ₹100 minimum balance validation)
    public void withdraw(String accNo, BigDecimal amount) {
        if (!ValidationUtils.isPositiveAmount(amount))
            throw new IllegalArgumentException("Invalid withdrawal amount");

        Account a = accountDao.findByAccountNumber(accNo);
        if (a == null)
            throw new IllegalArgumentException("Account not found: " + accNo);

        //  Minimum balance check: ₹100 must remain
        BigDecimal minBalance = new BigDecimal("100");
        if (a.getBalance().subtract(amount).compareTo(minBalance) < 0)
            throw new IllegalArgumentException("You must maintain a minimum balance of ₹100");

        if (a.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient balance");

        a.setBalance(a.getBalance().subtract(amount));
        accountDao.updateBalanceAndActivity(a);

        // Log withdrawal
        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.WITHDRAW, accNo, null, amount);
        txDao.saveTransaction(tx);
    }

    //  Transfer logic
    public void transfer(String fromAccNo, String toAccNo, BigDecimal amount) {
        if (!ValidationUtils.isPositiveAmount(amount))
            throw new IllegalArgumentException("Invalid transfer amount");

        Account from = accountDao.findByAccountNumber(fromAccNo);
        Account to = accountDao.findByAccountNumber(toAccNo);

        if (from == null || to == null)
            throw new IllegalArgumentException("One or both accounts not found");

        //  Minimum balance validation
        BigDecimal minBalance = new BigDecimal("100");
        if (from.getBalance().subtract(amount).compareTo(minBalance) < 0)
            throw new IllegalArgumentException("You must maintain a minimum balance of ₹100 after transfer");

        if (from.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds in source account");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountDao.updateBalanceAndActivity(from);
        accountDao.updateBalanceAndActivity(to);

        // Log transfer
        TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.TRANSFER, fromAccNo, toAccNo, amount);
        txDao.saveTransaction(tx);
    }

    //  Fetch account by account number
    public Account getAccount(String accNo) {
        return accountDao.findByAccountNumber(accNo);
    }

    //  Log transaction (used by BankingApp)
    public void logTransaction(TransactionRecord tx) {
        txDao.saveTransaction(tx);
    }

    //  Apply monthly interest (optional feature)
    public void applyMonthlyInterest() {
        List<Account> accounts = accountDao.listAllAccounts();
        BigDecimal monthlyRate = new BigDecimal("0.005"); // 0.5% per month

        for (Account a : accounts) {
            BigDecimal interest = a.getBalance().multiply(monthlyRate);
            a.setBalance(a.getBalance().add(interest));
            accountDao.updateBalanceAndActivity(a);

            TransactionRecord tx = new TransactionRecord(TransactionRecord.TxType.DEPOSIT, null, a.getAccountNumber(), interest);
            txDao.saveTransaction(tx);
        }
    }

    //  Flag inactive accounts (optional)
    public void flagInactiveAccounts() {
        List<Account> accounts = accountDao.listAllAccounts();
        LocalDate today = LocalDate.now();

        for (Account a : accounts) {
            LocalDateTime last = accountDao.getLastActivity(a.getAccountNumber());
            if (last != null && ChronoUnit.DAYS.between(last.toLocalDate(), today) > 365) {
                a.setStatus("INACTIVE");
                accountDao.updateAccountStatus(a);
            }
        }
    }

    //  List all accounts (used in console and tests)
    public List<Account> listAllAccounts() {
        return accountDao.listAllAccounts();
    }

    //  Delete account by account number
    public void deleteAccount(String accNo) {
        accountDao.deleteAccount(accNo);
    }

    //  Show mini statement (list of recent transactions)
    public void showMiniStatement(String accNo) {
        List<TransactionRecord> transactions = txDao.getTransactionsByAccount(accNo);
        System.out.println("\nMini Statement for Account: " + accNo);
        if (transactions.isEmpty()) {
            System.out.println("No recent transactions found.");
        } else {
            for (TransactionRecord tx : transactions) {
                System.out.println(tx);
            }
        }
    }

    //  NEW — Get current balance
    public BigDecimal getBalance(String accNo) {
        Account a = accountDao.findByAccountNumber(accNo);
        if (a == null) {
            throw new IllegalArgumentException("Account not found: " + accNo);
        }
        return a.getBalance();
    }

    //  NEW — Verify PIN (for secure access)
    public boolean verifyPin(String accNo, String enteredPin) {
        Account a = accountDao.findByAccountNumber(accNo);
        if (a == null) {
            System.out.println(" Account not found: " + accNo);
            return false;
        }
        String storedPin = a.getPassword(); // Assuming password = PIN
        return storedPin != null && storedPin.equals(enteredPin);
    }
}
