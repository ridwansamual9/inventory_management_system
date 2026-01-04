/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package inventory.managment.system;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class LoginPageController implements Initializable {
     private Stage stage;
     private Scene scene;
     private Parent root;
    private Label label;
    @FXML
    private Button loginbtn;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label inactiveMessage;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inactiveMessage.setVisible(false);
    }    

    @FXML
private void loginhundler(ActionEvent event) throws IOException {
    String u = username.getText() == null ? "" : username.getText().trim();
    String p = password.getText() == null ? "" : password.getText().trim();

    // Reset styles
    username.setStyle("");
    password.setStyle("");
    inactiveMessage.setVisible(false);

    if (u.isEmpty() || p.isEmpty()) {
        if (u.isEmpty()) {
            username.setStyle("-fx-border-color: red;");
        }
        if (p.isEmpty()) {
            password.setStyle("-fx-border-color: red;");
        }
        return;
    }

    boolean isValid = false;
    boolean isActive = false;
    String userRole = null;
    boolean userExists = false;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ims_db?serverTimezone=UTC",
                "root", "0905225438")) {

            // Check if username exists
            try (PreparedStatement checkUser = con.prepareStatement(
                    "SELECT 1 FROM Users WHERE username = ?")) {
                checkUser.setString(1, u);
                try (ResultSet rs = checkUser.executeQuery()) {
                    if (rs.next()) {
                        userExists = true;
                    }
                }
            }

            // Actual validation and get role and is_active
            try (PreparedStatement prsm = con.prepareStatement(
                    "SELECT role, is_active FROM Users WHERE username = ? AND password = ?")) {
                prsm.setString(1, u);
                prsm.setString(2, p);
                try (ResultSet rs = prsm.executeQuery()) {
                    if (rs.next()) {
                        isValid = true;
                        userRole = rs.getString("role");
                        isActive = rs.getBoolean("is_active");
                        System.out.println("User role: " + userRole + ", is_active: " + isActive);
                    }
                }
            }
        }
    } catch (SQLException | ClassNotFoundException ex) {
        ex.printStackTrace();
    }

    if (isValid) {
        if (isActive) {
            String dashboardFxml;
            if ("MANAGER".equals(userRole)) {
                dashboardFxml = "ManagerDashboard.fxml";
            } else {
                dashboardFxml = "DataEntryDashboard.fxml";
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            inactiveMessage.setVisible(true);
        }
    } else {
        if (userExists) {
            password.setStyle("-fx-border-color: red;");
        } else {
            username.setStyle("-fx-border-color: red;");
        }
    }
}

    
    
}
