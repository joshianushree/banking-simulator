import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.List;


public class BankingConsole {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountManager manager = new AccountManager();

        System.out.println("====================================");
        System.out.println("🏦  Welcome to Anushree Banking CLI  ");
        System.out.println("====================================");

        boolean exit = false;
        while (!exit) {
            System.out.println("\nMain Menu:");
            System.out.println("1) Manage Accounts");
            System.out.println("2) Transactions");
            System.out.println("3) Exit");
            System.out.print("Enter choice (1-3): ");
            String mainChoice = sc.nextLine().trim();

            switch (mainChoice) {
                case "1":
                    manageAccountsMenu(sc, manager);
                    break;
                case "2":
                    transactionsMenu(sc, manager);
                    break;
                case "3":
                    System.out.println("\nThank you for using Anushree Banking CLI! 👋");
                    exit = true;
                    break;
                default:
                    System.out.println("⚠️ Invalid choice. Please enter 1, 2, or 3.");
            }
        }

        sc.close();
    }

    private static void manageAccountsMenu(Scanner sc, AccountManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\nManage Accounts:");
            System.out.println("1) Create Account");
            System.out.println("2) Remove Account");
            System.out.println("3) View All Accounts");
            System.out.println("4) Back");
            System.out.print("Enter choice (1-4): ");
            String c = sc.nextLine().trim();

            switch (c) {
                case "1":
                    createAccountFlow(sc, manager); // ✅ calls the validation method before inserting
                    break;


                case "2":
                    System.out.print("Enter account number to remove: ");
                    String rem = sc.nextLine().trim();

                    // ✅ Validate 11-digit account number before deleting
                    if (!rem.matches("\\d{11}")) {
                        System.out.println("❌ Invalid account number. Must be exactly 11 digits.");
                        break;
                    }

                    manager.deleteAccount(rem);
                    System.out.println("✅ Account Number deleted successfully.");
                    break;

                case "3":
                    List<Account> accounts = manager.listAllAccounts();
                    if (accounts.isEmpty()) {
                        System.out.println("⚠️ No accounts found.");
                    } else {
                        System.out.println("\n---- Existing Accounts ----");
                        for (Account a : accounts) {
                            System.out.printf(
                                    "%s | %-20s | %-10s | ₹%-10s | Type: %-8s | Status: %s%n",
                                    a.getAccountNumber(),
                                    a.getHolderName(),
                                    a.getEmail(),
                                    a.getBalance(),
                                    a.getAccountType(),
                                    a.getStatus()
                            );
                        }
                    }
                    break;

                case "4":
                    back = true;
                    break;

                default:
                    System.out.println("⚠️ Invalid choice. Please try again.");
            }
        }
    }



    private static void transactionsMenu(Scanner sc, AccountManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Transactions Menu ===");
            System.out.println("1) Deposit");
            System.out.println("2) Withdraw");
            System.out.println("3) Transfer");
            System.out.println("4) Mini Statement (Last 5)");
            System.out.println("5) Check Balance");
            System.out.println("6) Back");
            System.out.print("Enter choice (1-6): ");
            String c = sc.nextLine().trim();

            try {
                switch (c) {
                    case "1":
                        System.out.print("Enter account number: ");
                        String depAcc = sc.nextLine().trim();
                        System.out.print("Enter amount to deposit: ");
                        BigDecimal depAmt = getBigDecimalFromUser(sc);
                        manager.deposit(depAcc, depAmt);
                        System.out.println("✅ Deposit successful! Current Balance: ₹" + manager.getBalance(depAcc));
                        break;

                    case "2":
                        System.out.print("Enter account number: ");
                        String wAcc = sc.nextLine().trim();
                        System.out.print("Enter amount to withdraw: ");
                        BigDecimal wAmt = getBigDecimalFromUser(sc);
                        manager.withdraw(wAcc, wAmt);
                        System.out.println("✅ Withdrawal successful! Current Balance: ₹" + manager.getBalance(wAcc));
                        break;

                    case "3":
                        System.out.print("Enter FROM account number: ");
                        String from = sc.nextLine().trim();
                        System.out.print("Enter TO account number: ");
                        String to = sc.nextLine().trim();
                        System.out.print("Enter amount to transfer: ");
                        BigDecimal tAmt = getBigDecimalFromUser(sc);
                        manager.transfer(from, to, tAmt);
                        System.out.println("✅ Transfer successful!");
                        System.out.println("Your updated balance: ₹" + manager.getBalance(from));
                        break;

                    case "4": // ✅ Mini Statement with PIN verification
                        System.out.print("Enter account number: ");
                        String acc = sc.nextLine().trim();
                        System.out.print("Enter your PIN: ");
                        String pin = sc.nextLine().trim();

                        if (manager.verifyPin(acc, pin)) {
                            manager.showMiniStatement(acc);
                        } else {
                            System.out.println("❌ Incorrect PIN. Access denied.");
                        }
                        break;

                    case "5": // ✅ Balance check with PIN
                        System.out.print("Enter account number: ");
                        String balAcc = sc.nextLine().trim();
                        System.out.print("Enter your PIN: ");
                        String balPin = sc.nextLine().trim();

                        if (manager.verifyPin(balAcc, balPin)) {
                            BigDecimal balance = manager.getBalance(balAcc);
                            System.out.println("💰 Current Balance: ₹" + balance);
                        } else {
                            System.out.println("❌ Incorrect PIN. Access denied.");
                        }
                        break;

                    case "6":
                        back = true;
                        break;

                    default:
                        System.out.println("⚠️ Invalid choice. Please try again.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }
    }



    private static void createAccountFlow(Scanner sc, AccountManager manager) {
        System.out.println("\n--- Create New Account ---");

        System.out.print("Enter 11-digit Account Number: ");
        String accountNumber = sc.nextLine().trim();
        if (!accountNumber.matches("\\d{11}")) {
            System.out.println("❌ Account number must be exactly 11 digits.");
            return;
        }

        System.out.print("Enter Holder Name: ");
        String holderName = sc.nextLine().trim();
        if (holderName.isEmpty() || !holderName.matches("^[A-Za-z ]+$")) {
            System.out.println("❌ Holder name must contain only alphabets and spaces (no numbers/symbols).");
            return;
        }

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("❌ Invalid email format. Please enter a valid email (e.g., user@gmail.com).");
            return;
        }

        System.out.print("Enter Initial Balance: ");
        BigDecimal balance;
        try {
            balance = new BigDecimal(sc.nextLine().trim());
            if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println(" Deposit amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount. Enter a valid number.");
            return;
        }

        System.out.print("Enter Account Type (SAVINGS/CURRENT): ");
        String accountType = sc.nextLine().trim().toUpperCase();
        if (!accountType.equals("SAVINGS") && !accountType.equals("CURRENT")) {
            System.out.println("❌ Invalid account type. Please enter SAVINGS or CURRENT.");
            return;
        }

        System.out.print("Set 4-digit PIN: ");
        String pin = sc.nextLine().trim();
        if (!pin.matches("\\d{4}")) {
            System.out.println("❌ PIN must be exactly 4 digits.");
            return;
        }

        // ✅ Create Account object using constructor
        Account account = new Account(
                accountNumber,
                holderName,
                email,
                balance,
                accountType,
                pin
        );

        account.setCreatedAt(LocalDateTime.now());
        manager.createAccount(account);
    }



    private static BigDecimal getBigDecimalFromUser(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return new BigDecimal(s);
            } catch (Exception e) {
                System.out.print("Invalid amount. Enter a numeric value: ");
            }
        }
    }
}
