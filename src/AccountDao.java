import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private final String url = "jdbc:mysql://localhost:3306/banking_simulator";
    private final String user = "root";   // change if needed
    private final String pass = "system"; // change to your MySQL password

    public AccountDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    // ------------------- CREATE -------------------
    public void createAccount(Account a) {
        // ✅ Basic null check
        if (a == null) {
            System.out.println("❌ Account details cannot be null.");
            return;
        }

        // ✅ Validate Account Number (exactly 11 digits)
        String accNum = a.getAccountNumber();
        if (accNum == null || !accNum.matches("\\d{11}")) {
            System.out.println("❌ Account number must be exactly 11 digits.");
            return;
        }

        // ✅ Prevent duplicate account numbers
        if (findByAccountNumber(accNum) != null) {
            System.out.println("❌ Account already exists: " + accNum);
            return;
        }

        // ✅ Validate Holder Name (alphabets + spaces)
        String name = a.getHolderName();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Holder name cannot be empty.");
            return;
        }
        if (!name.matches("^[A-Za-z ]+$")) {
            System.out.println("❌ Holder name must contain only alphabets and spaces.");
            return;
        }

        // ✅ Validate Email (simple regex)
        String email = a.getEmail();
        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email cannot be empty.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("❌ Invalid email format. Example: user@gmail.com");
            return;
        }

        // ✅ Validate PIN (exactly 4 digits)
        String pin = a.getPin();
        if (pin == null || !pin.matches("\\d{4}")) {
            System.out.println("❌ PIN must be exactly 4 digits.");
            return;
        }

        // ✅ Validate Account Type
        String type = a.getAccountType();
        if (type == null || type.trim().isEmpty()) {
            type = "SAVINGS";
        }

        // ✅ SQL Insert Query (matches DB columns)
        String sql = "INSERT INTO accounts " +
                "(account_number, holder_name, email, balance, created_at, account_type, pin, last_activity, status, failed_attempts, is_locked) " +
                "VALUES (?,?,?,?,?,?,?,?, 'ACTIVE', 0, FALSE)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ps.setString(2, name.trim());
            ps.setString(3, email.trim());
            ps.setBigDecimal(4, a.getBalance());
            ps.setTimestamp(5, Timestamp.valueOf(a.getCreatedAt()));
            ps.setString(6, type.trim().toUpperCase());
            ps.setString(7, pin.trim());
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("✅ Account created successfully: " + accNum);
        } catch (SQLException e) {
            System.out.println("❌ Database error while creating account.");
            e.printStackTrace();
        }
    }


    // ------------------- READ -------------------
    public Account findByAccountNumber(String accNum) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("holder_name");
                String email = rs.getString("email");
                BigDecimal balance = rs.getBigDecimal("balance");
                LocalDateTime createdAt = rs.getTimestamp("created_at")
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                String accType = rs.getString("account_type");
                String pin = rs.getString("pin");

                Account a = new Account(accNum, name, email, balance, accType, pin);
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> listAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String accNum = rs.getString("account_number");
                String name = rs.getString("holder_name");
                String email = rs.getString("email");
                BigDecimal balance = rs.getBigDecimal("balance");
                String accType = rs.getString("account_type");
                String pin = rs.getString("pin");

                Account a = new Account(accNum, name, email, balance, accType, pin);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ------------------- UPDATE -------------------
    public void updateBalanceAndActivity(Account account) {
        String sql = "UPDATE accounts SET balance = ?, last_activity = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, account.getBalance());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, account.getAccountNumber());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ NEW: method to match old updateBalance() calls
    public void updateBalance(Account account) {
        updateBalanceAndActivity(account); // redirect for backward compatibility
    }

    // ✅ NEW: method to match old updateAccountStatus() calls
    public void updateAccountStatus(Account account) {
        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "CLOSED");
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------- DELETE -------------------
    public boolean deleteAccount(String accNum) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------- PIN SECURITY -------------------
    public int getFailedAttempts(String accNum) {
        String sql = "SELECT failed_attempts FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("failed_attempts");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setFailedAttempts(String accNum, int attempts) {
        String sql = "UPDATE accounts SET failed_attempts = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attempts);
            ps.setString(2, accNum);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lockAccount(String accNum) {
        String sql = "UPDATE accounts SET is_locked = TRUE WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAccountLocked(String accNum) {
        String sql = "SELECT is_locked FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBoolean("is_locked");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------- ACTIVITY TRACKING -------------------
    public LocalDateTime getLastActivity(String accNum) {
        String sql = "SELECT last_activity FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("last_activity");
                if (ts != null) return ts.toLocalDateTime();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
