package until;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/quan_ly_dien_thoai";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static final String URL = EnvConfig.getOrDefault("DB_URL", DEFAULT_URL);
    private static final String USER = EnvConfig.getOrDefault("DB_USER", DEFAULT_USER);
    private static final String PASS = EnvConfig.getOrDefault("DB_PASSWORD", DEFAULT_PASS);

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println(">> [System] Connected to MySQL successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println(">> [Warning] Cannot connect to database.");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
