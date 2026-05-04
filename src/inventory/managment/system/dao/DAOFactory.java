package inventory.managment.system.dao;

/**
 * DAOFactory provides a central point for obtaining DAO instances.
 * Implements a simple Factory pattern to decouple controllers from DAO instantiation.
 */
public class DAOFactory {
    private static ItemDAO itemDAO;
    private static UserDAO userDAO;
    private static RequestDAO requestDAO;
    private static AuditDAO auditDAO;

    public static synchronized ItemDAO getItemDAO() {
        if (itemDAO == null) itemDAO = new ItemDAO();
        return itemDAO;
    }

    public static synchronized UserDAO getUserDAO() {
        if (userDAO == null) userDAO = new UserDAO();
        return userDAO;
    }

    public static synchronized RequestDAO getRequestDAO() {
        if (requestDAO == null) requestDAO = new RequestDAO();
        return requestDAO;
    }

    public static synchronized AuditDAO getAuditDAO() {
        if (auditDAO == null) auditDAO = new AuditDAO();
        return auditDAO;
    }
}
