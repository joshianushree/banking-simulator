import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private final String url = "jdbc:mysql://localhost:3306/banking_simulator";
    private final String user = "root";
    private final String pass = "system";

    public TransactionDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    // ✅ Save a new transaction
    public void saveTransaction(TransactionRecord tx) {
        String sql = "INSERT INTO transactions (tx_id, tx_type, from_account, to_account, amount, category, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tx.getTxId());
            ps.setString(2, tx.getTxType().name());
            ps.setString(3, tx.getFromAccount());
            ps.setString(4, tx.getToAccount());
            ps.setBigDecimal(5, tx.getAmount());
            ps.setString(6, tx.getCategory());
            ps.setTimestamp(7, Timestamp.valueOf(tx.getCreatedAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Fetch last N transactions for an account
    public List<TransactionRecord> fetchLastNForAccount(String accNum, int n) {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ps.setString(2, accNum);
            ps.setInt(3, n);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TransactionRecord.TxType type = TransactionRecord.TxType.valueOf(rs.getString("tx_type"));
                String from = rs.getString("from_account");
                String to = rs.getString("to_account");
                BigDecimal amount = rs.getBigDecimal("amount");
                String category = rs.getString("category");
                Timestamp ts = rs.getTimestamp("created_at");

                TransactionRecord tr = new TransactionRecord(type, from, to, amount, category);
                tr.setCreatedAt(ts.toLocalDateTime()); // ensure proper timestamp display
                list.add(tr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Added: get all transactions for an account (fixes AccountManager error)
    public List<TransactionRecord> getTransactionsByAccount(String accNum) {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accNum);
            ps.setString(2, accNum);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TransactionRecord.TxType type = TransactionRecord.TxType.valueOf(rs.getString("tx_type"));
                String from = rs.getString("from_account");
                String to = rs.getString("to_account");
                BigDecimal amount = rs.getBigDecimal("amount");
                String category = rs.getString("category");
                Timestamp ts = rs.getTimestamp("created_at");

                TransactionRecord tr = new TransactionRecord(type, from, to, amount, category);
                tr.setCreatedAt(ts.toLocalDateTime());
                list.add(tr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
