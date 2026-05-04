package inventory.managment.system.controller;

import inventory.managment.system.dao.DAOFactory;
import inventory.managment.system.dao.UserDAO;
import inventory.managment.system.model.User;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Controller for the User Management screen.
 * Strictly follows MVC pattern and uses DAOFactory.
 */
public class UserManagementController implements Initializable {

    @FXML private Button homebtn;
    @FXML private Button addUsersbtn;
    @FXML private ListView<String> userList;

    private final UserDAO userDAO = DAOFactory.getUserDAO();
    private final AuthService authService = AuthService.getInstance();
    private String currentUserRole;
    private int currentUserId;
    private List<User> users;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User current = authService.getCurrentUser();
        if (current != null) {
            currentUserRole = current.getRole();
            currentUserId = current.getId();
        }
        loadUsers();
    }

    private void loadUsers() {
        try {
            users = userDAO.getAllUsers();
            ObservableList<String> list = FXCollections.observableArrayList();
            for (User u : users) {
                list.add(String.format("ID: %d | %s | %s | %s", u.getId(), u.getUsername(), u.getRole(), u.isActive() ? "Active" : "Inactive"));
            }
            userList.setItems(list);
            userList.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    int idx = userList.getSelectionModel().getSelectedIndex();
                    if (idx >= 0 && idx < users.size()) editUser(users.get(idx));
                }
            });
        } catch (SQLException ex) {
            showAlert("Error", "Load failed.");
        }
    }

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
    }

    @FXML
    private void addUsersbtnHundlear(ActionEvent event) {
        if (!"MANAGER".equals(currentUserRole)) return;
        showAddUserDialog();
    }

    private void showAddUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField userF = new TextField();
        PasswordField passF = new PasswordField();
        ComboBox<String> roleC = new ComboBox<>();
        roleC.getItems().addAll("DATA_ENTRY", "MANAGER");
        roleC.setValue("DATA_ENTRY");

        grid.add(new Label("Username:"), 0, 0); grid.add(userF, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passF, 1, 1);
        grid.add(new Label("Role:"), 0, 2); grid.add(roleC, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    userDAO.addUser(userF.getText(), passF.getText(), roleC.getValue(), true);
                    loadUsers();
                } catch (SQLException ex) {
                    showAlert("Error", ex.getMessage());
                }
            }
            return btn;
        });
        dialog.showAndWait();
    }

    private void editUser(User user) {
        // Implementation similar to legacy with clean code improvements
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Edit User");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField userF = new TextField(user.getUsername());
        PasswordField passF = new PasswordField();
        passF.setPromptText("Empty for same");
        CheckBox activeC = new CheckBox("Active");
        activeC.setSelected(user.isActive());

        grid.add(new Label("Username:"), 0, 0); grid.add(userF, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passF, 1, 1);
        grid.add(activeC, 1, 2);

        d.getDialogPane().setContent(grid);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    userDAO.updateUser(user.getId(), userF.getText(), passF.getText().isEmpty() ? null : passF.getText(), activeC.isSelected());
                    loadUsers();
                } catch (SQLException ex) {
                    showAlert("Error", ex.getMessage());
                }
            }
            return btn;
        });
        d.showAndWait();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.show();
    }
}

