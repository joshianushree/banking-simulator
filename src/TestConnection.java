import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banking_simulator", // replace with your DB name
                    "root",                                          // your MySQL username
                    "system"                                  // your MySQL password
            );
            System.out.println(" Connection successful!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
