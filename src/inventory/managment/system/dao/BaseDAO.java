package inventory.managment.system.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * BaseDAO provides centralized connection management for all DAO classes.
 */
public abstract class BaseDAO {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
        }
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        // Try to load from root of classpath
        try (InputStream input = BaseDAO.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                // Try relative to package if root failed
                try (InputStream input2 = BaseDAO.class.getResourceAsStream("/db.properties")) {
                    if (input2 == null) {
                        // Fallback for local testing if not in classpath
                        URL = "jdbc:mysql://localhost:3306/ims_db?serverTimezone=UTC";
                        USER = "root";
                        PASSWORD = "0905225438";
                        System.err.println("db.properties not found, using legacy defaults.");
                        return;
                    }
                    props.load(input2);
                }
            } else {
                props.load(input);
            }
            
            URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/ims_db?serverTimezone=UTC");
            USER = props.getProperty("db.user", "root");
            PASSWORD = props.getProperty("db.password", "0905225438");
            System.out.println("Database configuration loaded from db.properties");
        } catch (IOException ex) {
            System.err.println("Error loading db.properties: " + ex.getMessage());
            // Safe fallbacks
            URL = "jdbc:mysql://localhost:3306/ims_db?serverTimezone=UTC";
            USER = "root";
            PASSWORD = "0905225438";
        }
    }

    /**
     * Obtains a fresh connection to the database.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // In a production environment, we would use a Connection Pool (e.g. HikariCP) here.
}
