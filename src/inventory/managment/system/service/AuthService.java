package inventory.managment.system.service;

import inventory.managment.system.dao.UserDAO;
import inventory.managment.system.model.User;
import java.sql.SQLException;
import inventory.managment.system.dao.DAOFactory;

/**
 * AuthService handles the business logic for user authentication and session.
 */
public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO;
    private static User currentUser;

    private AuthService() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Authenticates a user.
     * @param username The username
     * @param password The password
     * @return The authenticated User, or null if invalid
     * @throws SQLException if database error occurs
     */
    public User authenticate(String username, String password) throws SQLException {
        User user = userDAO.validateUser(username, password);
        if (user != null) {
            currentUser = user;
        }
        return user;
    }

    /**
     * Checks if a username exists.
     * @param username The username to check
     * @return true if exists
     * @throws SQLException if database error occurs
     */
    public boolean userExists(String username) throws SQLException {
        return userDAO.getUserByUsername(username) != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
