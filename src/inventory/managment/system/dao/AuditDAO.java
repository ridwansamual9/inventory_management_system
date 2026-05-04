package inventory.managment.system.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO extends BaseDAO {

    public void logAction(Connection con, String actionType, String itemType, int userId, int requestId) throws SQLException {
        String query = "INSERT INTO Audit (action_date, action_type, item_type, performed_by_user_id, request_id) VALUES (NOW(), ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, actionType);
            ps.setString(2, itemType);
            ps.setInt(3, userId);
            ps.setInt(4, requestId);
            ps.executeUpdate();
        }
    }

    public List<String> getAuditLogs(String actionTypeFilter) throws SQLException {
        List<String> auditLogs = new ArrayList<>();
        String query;
        
        if (actionTypeFilter == null || actionTypeFilter.isEmpty() || "All Actions".equals(actionTypeFilter)) {
            query = "SELECT Audit.action_type, Audit.item_type, Audit.action_date, Users.username, Request.request_id " +
                    "FROM Audit " +
                    "LEFT JOIN Users ON Audit.performed_by_user_id = Users.id " +
                    "LEFT JOIN Request ON Audit.request_id = Request.request_id " +
                    "ORDER BY Audit.action_date DESC";
        } else {
            query = "SELECT Audit.action_type, Audit.item_type, Audit.action_date, Users.username, Request.request_id " +
                    "FROM Audit " +
                    "LEFT JOIN Users ON Audit.performed_by_user_id = Users.id " +
                    "LEFT JOIN Request ON Audit.request_id = Request.request_id " +
                    "WHERE Audit.action_type = ? " +
                    "ORDER BY Audit.action_date DESC";
        }
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            if (actionTypeFilter != null && !actionTypeFilter.isEmpty() && !"All Actions".equals(actionTypeFilter)) {
                ps.setString(1, actionTypeFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String actionType = rs.getString("action_type");
                    String itemType = rs.getString("item_type");
                    Timestamp actionDate = rs.getTimestamp("action_date");
                    String username = rs.getString("username");
                    int requestId = rs.getInt("request_id");
                    
                    String dateStr = "";
                    if (actionDate != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dateStr = sdf.format(actionDate);
                    }
                    
                    String logEntry = String.format("%s | %s | Request #%d | by %s | %s", 
                        actionType != null ? actionType : "UNKNOWN", 
                        itemType != null ? itemType : "UNKNOWN", 
                        requestId, 
                        username != null ? username : "Unknown", 
                        dateStr);
                    auditLogs.add(logEntry);
                }
            }
        }
        return auditLogs;
    }
}
