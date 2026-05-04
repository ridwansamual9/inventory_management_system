package inventory.managment.system.dao;

import inventory.managment.system.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO handles all database operations related to the Users table.
 */
public class UserDAO extends BaseDAO {

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT id, username, role, is_active FROM Users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserById(int id) throws SQLException {
        String query = "SELECT id, username, role, is_active FROM Users WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public User validateUser(String username, String password) throws SQLException {
        String query = "SELECT id, username, role, is_active FROM Users WHERE username = ? AND password = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public int addUser(String username, String password, String role, boolean isActive) throws SQLException {
        String query = "INSERT INTO Users (username, password, role, is_active) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setBoolean(4, isActive);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void updateUser(int userId, String username, String password, boolean isActive) throws SQLException {
        String query;
        boolean updatePassword = password != null && !password.isEmpty();
        
        if (updatePassword) {
            query = "UPDATE Users SET username = ?, password = ?, is_active = ? WHERE id = ?";
        } else {
            query = "UPDATE Users SET username = ?, is_active = ? WHERE id = ?";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            if (updatePassword) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setBoolean(3, isActive);
                ps.setInt(4, userId);
            } else {
                ps.setString(1, username);
                ps.setBoolean(2, isActive);
                ps.setInt(3, userId);
            }
            ps.executeUpdate();
        }
    }

    public void setUserActive(int userId, boolean isActive) throws SQLException {
        String query = "UPDATE Users SET is_active = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean usernameExists(String username, int excludeUserId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ? AND id != ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("role"),
            rs.getBoolean("is_active")
        );
    }
}
