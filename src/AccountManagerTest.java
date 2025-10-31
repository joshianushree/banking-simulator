import java.math.BigDecimal;
import java.util.List;

public class AccountManagerTest {
    public static void main(String[] args) {
        System.out.println("Starting AccountManager test...\n");

        AccountManager manager = new AccountManager();

        //  Create accounts (using the latest Account constructor)
        Account acc1 = new Account(
                "A1001",
                "Anushree",
                "anushree@example.com",
                new BigDecimal("5000.00"),
                "SAVINGS",
                "1234"
        );

        Account acc2 = new Account(
                "A1002",
                "Sneha",
                "sneha@example.com",
                new BigDecimal("3000.00"),
                "CURRENT",
                "5678"
        );

        //  Create accounts in DB
        manager.createAccount(acc1);
        manager.createAccount(acc2);

        //  Display initial list of accounts
        System.out.println("\nInitial Account List:");
        List<Account> allAccounts = manager.listAllAccounts();
        for (Account a : allAccounts) {
            System.out.println(a);
        }

        //  Perform deposit
        System.out.println("\nDepositing 1000.00 to A1001...");
        manager.deposit("A1001", new BigDecimal("1000.00"));

        //  Perform withdrawal
        System.out.println("Withdrawing 500.00 from A1002...");
        manager.withdraw("A1002", new BigDecimal("500.00"));

        //  Perform transfer
        System.out.println("Transferring 2000.00 from A1001 to A1002...");
        manager.transfer("A1001", "A1002", new BigDecimal("2000.00"));

        //  Final account state
        System.out.println("\nFinal Account Balances:");
        allAccounts = manager.listAllAccounts();
        for (Account a : allAccounts) {
            System.out.printf("%s - %s - Balance: %s%n",
                    a.getAccountNumber(),
                    a.getHolderName(),
                    a.getBalance());
        }

        //  Show mini statement for A1001 and A1002
        System.out.println("\nMini Statement for A1001:");
        manager.showMiniStatement("A1001");

        System.out.println("\nMini Statement for A1002:");
        manager.showMiniStatement("A1002");

        System.out.println("\n Test completed successfully.");
    }
}
