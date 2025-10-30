import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AccountManagerTest {
    public static void main(String[] args) {
        try {
            String csvPath = "transactions.csv"; // created in project root
            AccountManager manager = new AccountManager(csvPath);

            // Create accounts for Anushree and another user
            Account a1 = new Account("A1001", "Anushree", "anushree@example.com", new BigDecimal("5000.00"));
            Account a2 = new Account("A1002", "Ravi", "ravi@example.com", new BigDecimal("2000.00"));

            manager.createAccount(a1);
            manager.createAccount(a2);

            System.out.println("Accounts created:");
            List<Account> accounts = manager.listAccounts();
            accounts.forEach(System.out::println);

            // Deposit to Anushree
            manager.deposit("A1001", new BigDecimal("1000.00"));
            System.out.println("After deposit, A1001 balance: " + manager.getAccount("A1001").getBalance());

            // Withdraw from Ravi
            manager.withdraw("A1002", new BigDecimal("500.00"));
            System.out.println("After withdraw, A1002 balance: " + manager.getAccount("A1002").getBalance());

            // Transfer from Anushree to Ravi
            manager.transfer("A1001", "A1002", new BigDecimal("1500.00"));
            System.out.println("After transfer, A1001 balance: " + manager.getAccount("A1001").getBalance());
            System.out.println("After transfer, A1002 balance: " + manager.getAccount("A1002").getBalance());

            // Show transactions.csv contents (print top lines)
            System.out.println("\n--- transactions.csv contents ---");
            Files.lines(Paths.get(csvPath)).forEach(System.out::println);

            System.out.println("\nTest completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
