/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package inventory.managment.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author REDWAN
 */
public class Databaseconnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ims_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "0905225438";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL Driver not found", ex);
        }
    }

    public static List<String> getRecentRequests() {
        List<String> requests = new ArrayList<>();
        String query = "SELECT CONCAT('Request ', request_id, ': ', request_type, ' for ', item_type, ' - ', status) AS description FROM Request ORDER BY request_date DESC LIMIT 3";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(rs.getString("description"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return requests;
    }

    public static List<String> getRecentItems() {
        List<String> items = new ArrayList<>();
        String query = "SELECT CONCAT(brand, ' ', model, ' (', serial_number, ')') AS item_name FROM Laptop ORDER BY id DESC LIMIT 3";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }

    // Add methods for statistics if needed
    public static int getTotalPendingRequests() {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'PENDING'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static int getTotalApprovedRequests() {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'APPROVED'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static int getTotalRejectedRequests() {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'REJECTED'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static int getTotalItems() {
        String query = "SELECT (SELECT COUNT(*) FROM Laptop) + (SELECT COUNT(*) FROM Desktop) + (SELECT COUNT(*) FROM Accessory) AS total";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static List<InventoryDisplayItem> getAllItems() throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        String laptopQuery = "SELECT 'Laptop' AS item_type, serial_number, brand, CONCAT(generation, ' ', model) AS model, `condition`, created_in_date FROM Laptop";
        String desktopQuery = "SELECT 'Desktop' AS item_type, tower_serial_number AS serial_number, '' AS brand, CONCAT(tower_generation, ' ', tower_model) AS model, desktop_condition AS `condition`, created_in_date FROM Desktop";
        String accessoryQuery = "SELECT type AS item_type, serial_number, brand, model, `condition`, created_in_date FROM Accessory";
        
        Connection con = getConnection();
        // Laptops
        try (PreparedStatement ps = con.prepareStatement(laptopQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String itemType = rs.getString("item_type");
                String serialNumber = rs.getString("serial_number");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String condition = rs.getString("condition");
                String addDate = rs.getString("created_in_date");
                items.add(new InventoryDisplayItem(itemType, serialNumber, brand, model, condition, addDate));
            }
        }
        // Desktops
        try (PreparedStatement ps = con.prepareStatement(desktopQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String itemType = rs.getString("item_type");
                String serialNumber = rs.getString("serial_number");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String condition = rs.getString("condition");
                String addDate = rs.getString("created_in_date");
                items.add(new InventoryDisplayItem(itemType, serialNumber, brand, model, condition, addDate));
            }
        }
        // Accessories
        try (PreparedStatement ps = con.prepareStatement(accessoryQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String itemType = rs.getString("item_type");
                String serialNumber = rs.getString("serial_number");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String condition = rs.getString("condition");
                String addDate = rs.getString("created_in_date");
                items.add(new InventoryDisplayItem(itemType, serialNumber, brand, model, condition, addDate));
            }
        }
        con.close();
        return items;
    }

    public static void insertRequest(String requestType, String itemType, String itemId, int userId) throws SQLException {
        String query = "INSERT INTO Request (user_id, item_id, request_type, item_type, status, request_date) VALUES (?, ?, ?, ?, 'PENDING', NOW())";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, itemId);
            ps.setString(3, requestType);
            ps.setString(4, itemType);
            ps.executeUpdate();
        }
    }
}
