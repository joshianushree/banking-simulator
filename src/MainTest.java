import java.math.BigDecimal;

public class MainTest {
    public static void main(String[] args) {
        try {
            // Create account for Anushree with initial balance 5000.00
            Account a = new Account("A1001", "Anushree", "anushree@example.com", new BigDecimal("5000.00"));
            System.out.println("Created account: " + a);

            // Deposit 1000.00
            a.deposit(new BigDecimal("1000.00"));
            System.out.println("After deposit 1000.00, balance: " + a.getBalance());

            // Withdraw 2000.00
            a.withdraw(new BigDecimal("2000.00"));
            System.out.println("After withdraw 2000.00, balance: " + a.getBalance());

            // Attempt invalid deposit
            try {
                a.deposit(new BigDecimal("-50.00"));
            } catch (Exception ex) {
                System.out.println("Expected error on invalid deposit: " + ex.getMessage());
            }

            // Attempt over-withdraw
            try {
                a.withdraw(new BigDecimal("10000.00"));
            } catch (Exception ex) {
                System.out.println("Expected error on over-withdraw: " + ex.getMessage());
            }

            System.out.println("Final account state: " + a);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
