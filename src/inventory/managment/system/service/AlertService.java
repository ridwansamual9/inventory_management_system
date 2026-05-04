package inventory.managment.system.service;

import inventory.managment.system.dao.BaseDAO;
import inventory.managment.system.dao.ItemDAO;
import inventory.managment.system.dao.RequestDAO;
import inventory.managment.system.dao.UserDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import inventory.managment.system.dao.DAOFactory;
import java.text.SimpleDateFormat;

/**
 * AlertService provides statistics and alerts for the dashboards.
 * It aggregates data from various DAOs.
 */
public class AlertService extends BaseDAO {
    private static AlertService instance;
    private final ItemDAO itemDAO;
    private final RequestDAO requestDAO;
    private final UserDAO userDAO;

    private AlertService() {
        this.itemDAO = DAOFactory.getItemDAO();
        this.requestDAO = DAOFactory.getRequestDAO();
        this.userDAO = DAOFactory.getUserDAO();
    }

    public static synchronized AlertService getInstance() {
        if (instance == null) {
            instance = new AlertService();
        }
        return instance;
    }

    public int getTotalPendingRequests() throws SQLException {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'PENDING'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getTotalApprovedRequests() throws SQLException {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'APPROVED'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getTotalRejectedRequests() throws SQLException {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'REJECTED'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getTotalItems() throws SQLException {
        return itemDAO.getTotalItemsCount();
    }

    public int getLaptopCount() throws SQLException {
        return itemDAO.getLaptopCount();
    }

    public int getDesktopCount() throws SQLException {
        return itemDAO.getDesktopCount();
    }

    public int getAccessoryCount() throws SQLException {
        return itemDAO.getAccessoryCount();
    }

    public int getLowStockCount() throws SQLException {
        return itemDAO.getLowStockCount(5);
    }

    public int getActiveUsersCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE is_active = 1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getUserActionAlertsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Request WHERE status = 'PENDING' AND request_type IN ('ADD_ITEM', 'UPDATES', 'DELETE')";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<String> getRecentRequests() throws SQLException {
        List<String> requests = new ArrayList<>();
        String query = "SELECT CONCAT('Request ', request_id, ': ', request_type, ' for ', item_type, ' - ', status) AS description FROM Request ORDER BY request_date DESC LIMIT 3";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(rs.getString("description"));
            }
        }
        return requests;
    }

    public List<String> getRecentItems() throws SQLException {
        List<String> items = new ArrayList<>();
        String query = "SELECT description FROM (" +
                       "  SELECT CONCAT('Laptop: ', brand, ' ', model) AS description, created_in_date FROM Laptop " +
                       "  UNION ALL " +
                       "  SELECT CONCAT('Desktop: ', tower_brand, ' ', tower_model) AS description, created_in_date FROM Desktop " +
                       "  UNION ALL " +
                       "  SELECT CONCAT('Accessory: ', brand, ' ', model) AS description, created_in_date FROM Accessory" +
                       ") AS combined ORDER BY created_in_date DESC LIMIT 3";
        
        // Note: I observed tower_brand might not exist in some schemas, adjusting to what's in ItemDAO
        // In ItemDAO lines 15-17: 
        // Laptop: brand, CONCAT(generation, ' ', model)
        // Desktop: tower_serial_number, CONCAT(tower_generation, ' ', tower_model)
        // Accessory: type, brand, model
        
        String safeQuery = "SELECT description FROM (" +
                           "  SELECT CONCAT('Laptop: ', brand, ' ', model) AS description, created_in_date FROM Laptop " +
                           "  UNION ALL " +
                           "  SELECT CONCAT('Desktop: ', tower_model) AS description, created_in_date FROM Desktop " +
                           "  UNION ALL " +
                           "  SELECT CONCAT(type, ': ', brand, ' ', model) AS description, created_in_date FROM Accessory" +
                           ") AS combined ORDER BY created_in_date DESC LIMIT 3";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(safeQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString("description"));
            }
        }
        return items;
    }

    public List<String> getRecentModifications() throws SQLException {
        List<String> alerts = new ArrayList<>();
        String query = "SELECT Request.request_id, Request.request_type, Request.item_type, Request.item_id, Request.request_date, Users.username " +
                       "FROM Request " +
                       "LEFT JOIN Users ON Request.user_id = Users.id " +
                       "WHERE Request.status = 'PENDING' " +
                       "ORDER BY Request.request_date DESC " +
                       "LIMIT 20";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String requestType = rs.getString("request_type");
                String itemType = rs.getString("item_type");
                String itemId = rs.getString("item_id");
                String username = rs.getString("username");
                Timestamp requestDate = rs.getTimestamp("request_date");
                
                String action = "";
                if ("ADD_ITEM".equals(requestType)) {
                    action = "New item addition request";
                } else if ("UPDATES".equals(requestType)) {
                    action = "Item update request";
                } else if ("DELETE".equals(requestType)) {
                    action = "Item deletion request";
                }
                
                String timeStr = (requestDate != null) ? new SimpleDateFormat("HH:mm").format(requestDate) : "N/A";
                String alert = String.format("%s: %s (%s) by %s at %s", 
                    action, itemType, itemId, username != null ? username : "Unknown", timeStr);
                alerts.add(alert);
            }
        }
        return alerts;
    }

    public void showError(String title, String msg) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
}
