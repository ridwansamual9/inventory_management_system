package inventory.managment.system.controller;

import inventory.managment.system.dao.DAOFactory;
import inventory.managment.system.dao.ItemDAO;
import inventory.managment.system.model.*;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.service.RequestService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for the Delete Item view.
 * Fixed typo from DelateItemController and refactored for MVC.
 */
public class DeleteItemController implements Initializable {

    @FXML private Button homebtn;
    @FXML private TextField shearchtextField;
    @FXML private Button searchbtn;
    @FXML private ListView<String> inventoryList;
    @FXML private Button delatebtn;
    @FXML private Label messageLabel;

    private final ItemDAO itemDAO = DAOFactory.getItemDAO();
    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private String foundItemType = null;
    private String foundItemId = null;
    private int currentUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getId();
        }
    }

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }

    @FXML
    private void searchbtnHundlear(ActionEvent event) {
        String sn = shearchtextField.getText().trim();
        if (sn.isEmpty()) {
            showMessage("Please enter a serial number", false);
            return;
        }

        searchbtn.setDisable(true);
        showMessage("Searching...", true);
        inventoryList.getItems().clear();
        
        AtomicBoolean found = new AtomicBoolean(false);

        // Multithreaded search for better responsiveness (demo of cratinal pattern or just improvement)
        new Thread(() -> {
            try {
                // Check Laptop
                Laptop l = itemDAO.getLaptopBySerial(sn);
                if (l != null) {
                    finishSearch(found, "LAPTOP", l.getSerialNumber(), String.format("Laptop: %s %s (SN: %s)", l.getBrand(), l.getModel(), l.getSerialNumber()));
                    return;
                }
                // Check Desktop
                Desktop d = itemDAO.getDesktopBySerial(sn);
                if (d != null) {
                    finishSearch(found, "DESKTOP", d.getTowerSerialNumber(), String.format("Desktop: %s %s (SN: %s)", d.getTowerGeneration(), d.getTowerModel(), d.getTowerSerialNumber()));
                    return;
                }
                // Check Accessory
                Accessory a = itemDAO.getAccessoryBySerial(sn);
                if (a != null) {
                    finishSearch(found, "ACCESSORY", a.getSerialNumber(), String.format("Accessory: %s %s (SN: %s)", a.getType(), a.getModel(), a.getSerialNumber()));
                    return;
                }
            } catch (SQLException e) {
                inventory.managment.system.service.AlertService.getInstance().showError("System Error", e.getMessage());
            } finally {
                Platform.runLater(() -> {
                    searchbtn.setDisable(false);
                    if (!found.get()) showMessage("Item not found.", false);
                });
            }
        }).start();
    }

    private void finishSearch(AtomicBoolean found, String type, String id, String desc) {
        found.set(true);
        Platform.runLater(() -> {
            foundItemType = type;
            foundItemId = id;
            inventoryList.setItems(FXCollections.observableArrayList(desc));
            showMessage("Item found! You can now request deletion.", true);
        });
    }

    @FXML
    private void deletebtnHundlear(ActionEvent event) {
        if (foundItemType == null || foundItemId == null) {
            showMessage("Search an item first.", false);
            return;
        }
        try {
            requestService.submitDeleteRequest(foundItemType, foundItemId, currentUserId);
            showMessage("Delete request submitted!", true);
            shearchtextField.clear();
            inventoryList.getItems().clear();
            foundItemType = null;
            foundItemId = null;
        } catch (SQLException ex) {
            showMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void showMessage(String msg, boolean success) {
        Platform.runLater(() -> {
            if (messageLabel != null) {
                messageLabel.setText(msg);
                messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
            } else {
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setContentText(msg);
                alert.showAndWait();
            }
        });
    }
}

