package inventory.managment.system.service;

import inventory.managment.system.dao.AuditDAO;
import inventory.managment.system.dao.BaseDAO;
import inventory.managment.system.dao.RequestDAO;
import inventory.managment.system.model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import inventory.managment.system.dao.DAOFactory;

/**
 * RequestService manages the business logic for inventory requests (Add, Update, Delete).
 * It handles transactional orchestration for approvals and rejections.
 */
public class RequestService extends BaseDAO {
    private static RequestService instance;
    private final RequestDAO requestDAO;
    private final AuditDAO auditDAO;

    private RequestService() {
        this.requestDAO = DAOFactory.getRequestDAO();
        this.auditDAO = DAOFactory.getAuditDAO();
    }

    public static synchronized RequestService getInstance() {
        if (instance == null) {
            instance = new RequestService();
        }
        return instance;
    }

    public List<Request> getPendingRequests() throws SQLException {
        return requestDAO.getPendingRequests();
    }

    public Request getRequestDetails(int requestId) throws SQLException {
        return requestDAO.getRequestDetails(requestId);
    }

    public void approveRequest(int requestId, int managerUserId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                
                Request detail = requestDAO.getRequestDetails(requestId);
                if (detail == null) throw new SQLException("Request not found");

                if ("ADD_ITEM".equals(detail.getRequestType())) {
                    executeAddItemApproval(con, detail);
                } else if ("UPDATES".equals(detail.getRequestType())) {
                    executeUpdateApproval(con, detail);
                } else if ("DELETE".equals(detail.getRequestType())) {
                    executeDeleteApproval(con, detail);
                }

                // Cleanup and finalize
                requestDAO.deleteTempEntries(con, requestId, detail.getItemType());
                requestDAO.updateRequestStatus(con, requestId, "APPROVED");
                
                String actionType = translateRequestTypeToAction(detail.getRequestType());
                auditDAO.logAction(con, actionType, detail.getItemType(), managerUserId, requestId);

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void rejectRequest(int requestId, int managerUserId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                
                Request detail = requestDAO.getRequestDetails(requestId);
                if (detail == null) throw new SQLException("Request not found");

                requestDAO.deleteTempEntries(con, requestId, detail.getItemType());
                requestDAO.updateRequestStatus(con, requestId, "REJECTED");
                auditDAO.logAction(con, "REJECT", detail.getItemType(), managerUserId, requestId);

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private void executeAddItemApproval(Connection con, Request detail) throws SQLException {
        String query = "";
        if ("LAPTOP".equalsIgnoreCase(detail.getItemType())) {
            query = "INSERT INTO Laptop (serial_number, brand, generation, model, `condition`, have_charger, have_mouse, created_in_date) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempSerialNumber());
                ps.setString(2, detail.getTempBrand());
                ps.setString(3, detail.getTempGeneration());
                ps.setString(4, detail.getTempModel());
                ps.setString(5, detail.getTempCondition());
                ps.setBoolean(6, detail.getTempHaveCharger() != null ? detail.getTempHaveCharger() : false);
                ps.setBoolean(7, detail.getTempHaveMouse() != null ? detail.getTempHaveMouse() : false);
                ps.executeUpdate();
            }
        } else if ("DESKTOP".equalsIgnoreCase(detail.getItemType())) {
            query = "INSERT INTO Desktop (tower_serial_number, tower_generation, tower_model, desktop_condition, keyboard_model, have_mouse, created_in_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempTowerSerialNumber());
                ps.setString(2, detail.getTempTowerGeneration());
                ps.setString(3, detail.getTempTowerModel());
                ps.setString(4, detail.getTempDesktopCondition());
                ps.setString(5, detail.getTempKeyboardModel());
                ps.setBoolean(6, detail.getTempDesktopHaveMouse() != null ? detail.getTempDesktopHaveMouse() : false);
                ps.executeUpdate();
            }
        } else if ("ACCESSORY".equalsIgnoreCase(detail.getItemType())) {
            query = "INSERT INTO Accessory (type, serial_number, brand, model, `condition`, created_in_date) VALUES (?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempAccessoryType());
                ps.setString(2, detail.getTempAccessorySerialNumber());
                ps.setString(3, detail.getTempAccessoryBrand());
                ps.setString(4, detail.getTempAccessoryModel());
                ps.setString(5, detail.getTempAccessoryCondition());
                ps.executeUpdate();
            }
        }
    }

    private void executeUpdateApproval(Connection con, Request detail) throws SQLException {
        String query = "";
        if ("LAPTOP".equalsIgnoreCase(detail.getItemType())) {
            query = "UPDATE Laptop SET brand = ?, generation = ?, model = ?, `condition` = ?, have_charger = ?, have_mouse = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempBrand());
                ps.setString(2, detail.getTempGeneration());
                ps.setString(3, detail.getTempModel());
                ps.setString(4, detail.getTempCondition());
                ps.setBoolean(5, detail.getTempHaveCharger() != null ? detail.getTempHaveCharger() : false);
                ps.setBoolean(6, detail.getTempHaveMouse() != null ? detail.getTempHaveMouse() : false);
                ps.setInt(7, detail.getTempOriginalItemId());
                ps.executeUpdate();
            }
        } else if ("DESKTOP".equalsIgnoreCase(detail.getItemType())) {
            query = "UPDATE Desktop SET tower_generation = ?, tower_model = ?, desktop_condition = ?, keyboard_model = ?, have_mouse = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempTowerGeneration());
                ps.setString(2, detail.getTempTowerModel());
                ps.setString(3, detail.getTempDesktopCondition());
                ps.setString(4, detail.getTempKeyboardModel());
                ps.setBoolean(5, detail.getTempDesktopHaveMouse() != null ? detail.getTempDesktopHaveMouse() : false);
                ps.setInt(6, detail.getTempOriginalItemId());
                ps.executeUpdate();
            }
        } else if ("ACCESSORY".equalsIgnoreCase(detail.getItemType())) {
            query = "UPDATE Accessory SET type = ?, brand = ?, model = ?, `condition` = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getTempAccessoryType());
                ps.setString(2, detail.getTempAccessoryBrand());
                ps.setString(3, detail.getTempAccessoryModel());
                ps.setString(4, detail.getTempAccessoryCondition());
                ps.setInt(5, detail.getTempOriginalItemId());
                ps.executeUpdate();
            }
        }
    }

    private void executeDeleteApproval(Connection con, Request detail) throws SQLException {
        String table = "";
        String snColumn = "";
        if ("LAPTOP".equalsIgnoreCase(detail.getItemType())) { table = "Laptop"; snColumn = "serial_number"; }
        else if ("DESKTOP".equalsIgnoreCase(detail.getItemType())) { table = "Desktop"; snColumn = "tower_serial_number"; }
        else if ("ACCESSORY".equalsIgnoreCase(detail.getItemType())) { table = "Accessory"; snColumn = "serial_number"; }

        if (!table.isEmpty()) {
            String query = "DELETE FROM " + table + " WHERE " + snColumn + " = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, detail.getItemId());
                ps.executeUpdate();
            }
        }
    }

    private String translateRequestTypeToAction(String requestType) {
        if ("ADD_ITEM".equals(requestType)) return "ADD_ITEM";
        if ("UPDATES".equals(requestType)) return "UPDATE";
        if ("DELETE".equals(requestType)) return "DELETE";
        return "UNKNOWN";
    }

    public void submitAddLaptopRequest(Laptop laptop, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, laptop.getSerialNumber(), "ADD_ITEM", "LAPTOP");
                requestDAO.insertTempLaptop(con, requestId, null, laptop);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitAddDesktopRequest(Desktop desktop, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, desktop.getTowerSerialNumber(), "ADD_ITEM", "DESKTOP");
                requestDAO.insertTempDesktop(con, requestId, null, desktop);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitAddAccessoryRequest(Accessory accessory, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, accessory.getSerialNumber(), "ADD_ITEM", "ACCESSORY");
                requestDAO.insertTempAccessory(con, requestId, null, accessory);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitUpdateLaptopRequest(int originalItemId, Laptop laptop, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, laptop.getSerialNumber(), "UPDATES", "LAPTOP");
                requestDAO.insertTempLaptop(con, requestId, originalItemId, laptop);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitUpdateDesktopRequest(int originalItemId, Desktop desktop, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, desktop.getTowerSerialNumber(), "UPDATES", "DESKTOP");
                requestDAO.insertTempDesktop(con, requestId, originalItemId, desktop);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitUpdateAccessoryRequest(int originalItemId, Accessory accessory, int userId) throws SQLException {
        try (Connection con = getConnection()) {
            try {
                con.setAutoCommit(false);
                int requestId = requestDAO.insertBaseRequest(con, userId, accessory.getSerialNumber(), "UPDATES", "ACCESSORY");
                requestDAO.insertTempAccessory(con, requestId, originalItemId, accessory);
                con.commit();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        }
    }

    public void submitDeleteRequest(String itemType, String serialNumber, int userId) throws SQLException {
        requestDAO.insertRequest("DELETE", itemType, serialNumber, userId);
    }

    public void submitUpdateRequest(String itemType, String serialNumber, int userId) throws SQLException {
        requestDAO.insertRequest("UPDATES", itemType, serialNumber, userId);
    }

    public List<Request> getRequestsByUserId(int userId) throws SQLException {
        return requestDAO.getRequestsByUserId(userId);
    }

    public void cancelRequest(int requestId) throws SQLException {
        requestDAO.cancelRequest(requestId);
    }
}
