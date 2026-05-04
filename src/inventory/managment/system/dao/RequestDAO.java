package inventory.managment.system.dao;

import inventory.managment.system.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestDAO handles database operations for Requests and Temporary tables.
 */
public class RequestDAO extends BaseDAO {

    public void insertRequest(String requestType, String itemType, String itemId, int userId) throws SQLException {
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

    public int insertBaseRequest(Connection con, int userId, String itemId, String requestType, String itemType) throws SQLException {
        String query = "INSERT INTO Request (user_id, item_id, request_type, item_type, status, request_date) VALUES (?, ?, ?, ?, 'PENDING', NOW())";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, itemId);
            ps.setString(3, requestType);
            ps.setString(4, itemType);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create request");
    }

    public void insertTempLaptop(Connection con, int requestId, Integer originalItemId, Laptop laptop) throws SQLException {
        String query = "INSERT INTO tempLaptop (request_id, item_id, serial_number, brand, generation, model, `condition`, have_charger, have_mouse) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, requestId);
            if (originalItemId != null) ps.setInt(2, originalItemId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, laptop.getSerialNumber());
            ps.setString(4, laptop.getBrand());
            ps.setString(5, laptop.getGeneration());
            ps.setString(6, laptop.getModel());
            ps.setString(7, laptop.getItemCondition().name());
            ps.setBoolean(8, laptop.isHaveCharger());
            ps.setBoolean(9, laptop.isHaveMouse());
            ps.executeUpdate();
        }
    }

    public void insertTempDesktop(Connection con, int requestId, Integer originalItemId, Desktop desktop) throws SQLException {
        String query = "INSERT INTO tempDesktop (request_id, item_id, tower_serial_number, tower_generation, tower_model, desktop_condition, keyboard_model, have_mouse) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, requestId);
            if (originalItemId != null) ps.setInt(2, originalItemId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, desktop.getTowerSerialNumber());
            ps.setString(4, desktop.getTowerGeneration());
            ps.setString(5, desktop.getTowerModel());
            ps.setString(6, desktop.getDesktopCondition().name());
            ps.setString(7, desktop.getKeyboardModel());
            ps.setBoolean(8, desktop.isHaveMouse());
            ps.executeUpdate();
        }
    }

    public void insertTempAccessory(Connection con, int requestId, Integer originalItemId, Accessory accessory) throws SQLException {
        String query = "INSERT INTO tempAccessory (request_id, item_id, type, serial_number, brand, model, `condition`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, requestId);
            if (originalItemId != null) ps.setInt(2, originalItemId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, accessory.getType().name());
            ps.setString(4, accessory.getSerialNumber());
            ps.setString(5, accessory.getBrand());
            ps.setString(6, accessory.getModel());
            ps.setString(7, accessory.getItemCondition().name());
            ps.executeUpdate();
        }
    }

    public List<Request> getPendingRequests() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM Request r LEFT JOIN Users u ON r.user_id = u.id WHERE r.status = 'PENDING' ORDER BY r.request_date DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    public Request getRequestDetails(int requestId) throws SQLException {
        String query = "SELECT r.*, u.username, " +
                "tl.serial_number AS tl_sn, tl.brand AS tl_brand, tl.generation AS tl_gen, tl.model AS tl_model, tl.condition AS tl_cond, tl.have_charger AS tl_charger, tl.have_mouse AS tl_mouse, tl.item_id AS tl_item_id, " +
                "td.tower_serial_number AS td_sn, td.tower_generation AS td_gen, td.tower_model AS td_model, td.desktop_condition AS td_cond, td.keyboard_model AS td_kb_model, td.have_mouse AS td_mouse, " +
                "ta.type AS ta_type, ta.serial_number AS ta_sn, ta.brand AS ta_brand, ta.model AS ta_model, ta.condition AS ta_cond " +
                "FROM Request r " +
                "LEFT JOIN Users u ON r.user_id = u.id " +
                "LEFT JOIN tempLaptop tl ON r.request_id = tl.request_id " +
                "LEFT JOIN tempDesktop td ON r.request_id = td.request_id " +
                "LEFT JOIN tempAccessory ta ON r.request_id = ta.request_id " +
                "WHERE r.request_id = ?";
                
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request req = mapResultSetToRequest(rs);
                    // Map temp data
                    req.setTempSerialNumber(rs.getString("tl_sn"));
                    req.setTempBrand(rs.getString("tl_brand"));
                    req.setTempGeneration(rs.getString("tl_gen"));
                    req.setTempModel(rs.getString("tl_model"));
                    req.setTempCondition(rs.getString("tl_cond"));
                    req.setTempHaveCharger(rs.getObject("tl_charger") != null ? rs.getBoolean("tl_charger") : null);
                    req.setTempHaveMouse(rs.getObject("tl_mouse") != null ? rs.getBoolean("tl_mouse") : null);
                    req.setTempOriginalItemId(rs.getObject("tl_item_id") != null ? rs.getInt("tl_item_id") : null);

                    req.setTempTowerSerialNumber(rs.getString("td_sn"));
                    req.setTempTowerGeneration(rs.getString("td_gen"));
                    req.setTempTowerModel(rs.getString("td_model"));
                    req.setTempDesktopCondition(rs.getString("td_cond"));
                    req.setTempKeyboardModel(rs.getString("td_kb_model"));
                    req.setTempDesktopHaveMouse(rs.getObject("td_mouse") != null ? rs.getBoolean("td_mouse") : null);

                    req.setTempAccessoryType(rs.getString("ta_type"));
                    req.setTempAccessorySerialNumber(rs.getString("ta_sn"));
                    req.setTempAccessoryBrand(rs.getString("ta_brand"));
                    req.setTempAccessoryModel(rs.getString("ta_model"));
                    req.setTempAccessoryCondition(rs.getString("ta_cond"));
                    
                    return req;
                }
            }
        }
        return null;
    }

    public void updateRequestStatus(Connection con, int requestId, String status) throws SQLException {
        String query = "UPDATE Request SET status = ? WHERE request_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    public void deleteTempEntries(Connection con, int requestId, String itemType) throws SQLException {
        String table = "";
        if ("LAPTOP".equalsIgnoreCase(itemType)) table = "tempLaptop";
        else if ("DESKTOP".equalsIgnoreCase(itemType)) table = "tempDesktop";
        else if ("ACCESSORY".equalsIgnoreCase(itemType)) table = "tempAccessory";
        
        if (!table.isEmpty()) {
            String query = "DELETE FROM " + table + " WHERE request_id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, requestId);
                ps.executeUpdate();
            }
        }
    }

    public List<Request> getRequestsByUserId(int userId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM Request WHERE user_id = ? ORDER BY request_date DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        }
        return requests;
    }

    public void cancelRequest(int requestId) throws SQLException {
        String query = "DELETE FROM Request WHERE request_id = ? AND status = 'PENDING'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    private Request mapResultSetToRequest(ResultSet rs) throws SQLException {
        Request req = new Request();
        req.setRequestId(rs.getInt("request_id"));
        req.setRequestType(rs.getString("request_type"));
        req.setItemType(rs.getString("item_type"));
        req.setItemId(rs.getString("item_id"));
        req.setStatus(rs.getString("status"));
        req.setRequestDate(rs.getTimestamp("request_date"));
        req.setUserId(rs.getInt("user_id"));
        // Check if username column exists in resultset
        try {
            req.setUsername(rs.getString("username"));
        } catch (SQLException ignore) {}
        return req;
    }
}
