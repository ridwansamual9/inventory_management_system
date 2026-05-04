package inventory.managment.system.model;

/**
 * Represents a user in the system.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private boolean isActive;

    public User() {}

    public User(int id, String username, String role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
