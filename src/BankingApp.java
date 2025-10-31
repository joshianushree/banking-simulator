import java.math.BigDecimal;
import java.util.List;

public class BankingApp {
    public static void main(String[] args) {
        System.out.println("=== Banking System Integration Test ===");

        try {
            // Use the no-arg constructor (keeps existing AccountDao behavior)
            AccountDao dao = new AccountDao();

            // Use the existing AccountManager (no-arg)
            AccountManager manager = new AccountManager();

            // Create sample accounts (Anushree required name used where needed)
            Account a1 = new Account("A1001", "Anushree", "anushree@example.com", new BigDecimal("10000.00"));
            Account a2 = new Account("A1002", "Riya", "riya@example.com", new BigDecimal("5000.00"));

            // Persist accounts to DB via DAO (won't duplicate if primary key exists - if it does, catch)
            try {
                dao.createAccount(a1);
            } catch (Exception ex) {
                System.out.println("Note: A1001 may already exist in DB - " + ex.getMessage());
            }
            try {
                dao.createAccount(a2);
            } catch (Exception ex) {
                System.out.println("Note: A1002 may already exist in DB - " + ex.getMessage());
            }

            System.out.println(" Accounts ensured in DB.");

            // Also ensure AccountManager has these accounts (if it keeps its own in-memory store)
            try { manager.createAccount(a1); } catch (Exception ignore) {}
            try { manager.createAccount(a2); } catch (Exception ignore) {}

            // ---- Deposit to A1001 ----
            BigDecimal depositAmt = new BigDecimal("2000.00");
            manager.deposit(a1.getAccountNumber(), depositAmt);                      // deposit via manager
            // fetch updated account from manager and persist to DB
            Account updatedA1 = manager.getAccount(a1.getAccountNumber());
            if (updatedA1 == null) updatedA1 = dao.findByAccountNumber(a1.getAccountNumber());
            dao.updateBalance(updatedA1);
            manager.logTransaction(new TransactionRecord(TransactionRecord.TxType.DEPOSIT, null, a1.getAccountNumber(), depositAmt));
            System.out.println(" Deposit done for " + a1.getAccountNumber() + " -> " + updatedA1.getBalance());

            // ---- Withdraw from A1002 ----
            BigDecimal withdrawAmt = new BigDecimal("1000.00");
            manager.withdraw(a2.getAccountNumber(), withdrawAmt);
            Account updatedA2 = manager.getAccount(a2.getAccountNumber());
            if (updatedA2 == null) updatedA2 = dao.findByAccountNumber(a2.getAccountNumber());
            dao.updateBalance(updatedA2);
            manager.logTransaction(new TransactionRecord(TransactionRecord.TxType.WITHDRAW, a2.getAccountNumber(), null, withdrawAmt));
            System.out.println(" Withdraw done for " + a2.getAccountNumber() + " -> " + updatedA2.getBalance());

            // ---- Transfer A1001 -> A1002 ----
            BigDecimal transferAmt = new BigDecimal("1500.00");
            manager.transfer(a1.getAccountNumber(), a2.getAccountNumber(), transferAmt);
            // fetch both updated accounts from manager (or DAO fallback)
            updatedA1 = manager.getAccount(a1.getAccountNumber());
            if (updatedA1 == null) updatedA1 = dao.findByAccountNumber(a1.getAccountNumber());
            updatedA2 = manager.getAccount(a2.getAccountNumber());
            if (updatedA2 == null) updatedA2 = dao.findByAccountNumber(a2.getAccountNumber());
            // persist both balances
            dao.updateBalance(updatedA1);
            dao.updateBalance(updatedA2);
            manager.logTransaction(new TransactionRecord(TransactionRecord.TxType.TRANSFER, a1.getAccountNumber(), a2.getAccountNumber(), transferAmt));
            System.out.println(" Transfer done from " + a1.getAccountNumber() + " → " + a2.getAccountNumber());

            // ---- Final readout ----
            System.out.println("\n--- Final Balances (from DB) ---");
            List<Account> all = dao.listAllAccounts();
            for (Account acc : all) {
                System.out.println(acc);
            }

            System.out.println("\n Integration test completed. Check transactions.csv and MySQL Workbench for records.");

        } catch (Exception e) {
            System.err.println("ERROR running BankingApp:");
            e.printStackTrace();
        }
    }
}
