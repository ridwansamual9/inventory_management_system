package inventory.managment.system.controller;

import inventory.managment.system.model.User;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Login Page.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class LoginPageController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button loginbtn;
    @FXML
    private Label inactiveMessage;

    private final AuthService authService = AuthService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inactiveMessage.setVisible(false);
    }

    @FXML
    private void loginhundler(ActionEvent event) {
        String user = username.getText().trim();
        String pass = password.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password.");
            return;
        }

        try {
            User authenticatedUser = authService.authenticate(user, pass);

            if (authenticatedUser != null) {
                if (authenticatedUser.isActive()) {
                    redirectUser(authenticatedUser, event);
                } else {
                    inactiveMessage.setVisible(true);
                }
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
            showAlert("Database Error", "Database connection error: " + ex.getMessage());
        }
    }

    private void redirectUser(User user, ActionEvent event) {
        String fxmlFile = "";
        String title = "";

        if ("MANAGER".equals(user.getRole())) {
            fxmlFile = "ManagerDashboard.fxml";
            title = "Manager Dashboard";
        } else if ("DATA_ENTRY".equals(user.getRole())) {
            fxmlFile = "DataEntryDashboard.fxml";
            title = "Data Entry Dashboard";
        }

        if (!fxmlFile.isEmpty()) {
            NavigationManager.navigate(event, fxmlFile, title);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

