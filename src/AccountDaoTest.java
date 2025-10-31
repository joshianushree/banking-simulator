import java.math.BigDecimal;

public class AccountDaoTest {
    public static void main(String[] args) {
        AccountDao dao = new AccountDao();

        try {
            // Create SAVINGS account for Anushree with PIN and system-generated account number
            Account newAcc = new Account(
                    null, // account number → generate automatically
                    "Anushree Joshi", // holder name
                    "anushree.joshi@example.com", // email
                    new BigDecimal("1500.00"), // initial deposit
                    "SAVINGS", // account type as String
                    "1234" // 4-digit PIN
            );

            // Create account in DAO (stored or persisted as per your implementation)
            dao.createAccount(newAcc);

            // Display all accounts currently available
            System.out.println("\nAll accounts:");
            dao.listAllAccounts().forEach(System.out::println);

            // Get first account and verify its PIN
            if (!dao.listAllAccounts().isEmpty()) {
                String someAccNumber = dao.listAllAccounts().get(0).getAccountNumber();
                Account fetched = dao.findByAccountNumber(someAccNumber);
                if (fetched != null) {
                    System.out.println("\nPIN verify (1234): " + fetched.verifyPin("1234"));
                } else {
                    System.out.println("\nAccount not found!");
                }
            } else {
                System.out.println("\nNo accounts found in the system.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
