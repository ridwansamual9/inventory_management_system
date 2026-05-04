package inventory.managment.system.controller;

import inventory.managment.system.service.AlertService;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the Data Entry Dashboard.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class DataEntryDashboardController implements Initializable {

    @FXML
    private VBox requestG1;
    @FXML
    private VBox requestG2;
    @FXML
    private VBox requestG3;
    @FXML
    private Button allRequestViewbtn;
    @FXML
    private VBox recentlyp1;
    @FXML
    private VBox recentlyp2;
    @FXML
    private VBox recentlyp3;

    @FXML
    private Button inventorybtn;
    @FXML
    private Button addItem;
    @FXML
    private Button updatebtn;
    @FXML
    private Button deletebtn;
    @FXML
    private Button myRequestbtn;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchbtn;
    @FXML
    private ComboBox<SearchBy> searchcomb;
    @FXML
    private Label totalPending;
    @FXML
    private Label totalAppr;
    @FXML
    private Label totalR;
    @FXML
    private Label totalItemn;
    @FXML
    private Button logoutbtn;

    private enum SearchBy { SERIAL_NUMBER, MODEL, BRAND }

    private final AlertService alertService = AlertService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchcomb.getItems().addAll(Arrays.asList(SearchBy.values()));
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            // Populate requests
            List<String> requests = alertService.getRecentRequests();
            VBox[] requestPanes = {requestG1, requestG2, requestG3};
            updatePanes(requestPanes, requests);

            // Populate recent items
            List<String> recentItems = alertService.getRecentItems();
            VBox[] itemPanes = {recentlyp1, recentlyp2, recentlyp3};
            updatePanes(itemPanes, recentItems);

            // Update statistics
            totalPending.setText(String.valueOf(alertService.getTotalPendingRequests()));
            totalAppr.setText(String.valueOf(alertService.getTotalApprovedRequests()));
            totalR.setText(String.valueOf(alertService.getTotalRejectedRequests()));
            totalItemn.setText(String.valueOf(alertService.getTotalItems()));
            
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    private void updatePanes(VBox[] panes, List<String> data) {
        for (int i = 0; i < panes.length; i++) {
            if (i < data.size() && panes[i] != null) {
                Label label = new Label(data.get(i));
                label.setWrapText(true);
                panes[i].getChildren().clear();
                panes[i].getChildren().add(label);
                panes[i].setVisible(true);
            } else if (panes[i] != null) {
                panes[i].setVisible(false);
            }
        }
    }

    @FXML
    private void requestViewHandler(ActionEvent event) {
        NavigationManager.navigate(event, "MyRequests.fxml", "My Requests");
    }

    @FXML
    private void deleteHandler(ActionEvent event) {
        NavigationManager.navigate(event, "DeleteItem.fxml", "Delete Item");
    }

    @FXML
    private void myRequestHandler(ActionEvent event) {
        NavigationManager.navigate(event, "MyRequests.fxml", "My Requests");
    }

    @FXML
    private void searchHandler(ActionEvent event) {
        try {
            // Special Case: Search redirection to InventoryList
            String path = "/inventory/managment/system/view/InventoryList.fxml";
            URL resource = getClass().getResource(path);
            if (resource == null) resource = getClass().getResource("/inventory/managment/system/InventoryList.fxml");
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InventoryListController controller = loader.getController();
            
            String searchText = searchField.getText().trim();
            String searchType = searchcomb.getValue() != null ? searchcomb.getValue().toString() : "SERIAL_NUMBER";
            
            controller.setUserRole("DATA_ENTRY");
            controller.setSearchParameters(searchText, searchType);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inventory Search");
            stage.show();
        } catch (Exception ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void addItemHandler(ActionEvent event) {
        NavigationManager.navigate(event, "AddItem.fxml", "Add Item");
    }

    @FXML
    private void updateHandler(ActionEvent event) {
        NavigationManager.navigate(event, "UpdateItem.fxml", "Update Item");
    }

    @FXML
    private void inventoryBtnHandler(ActionEvent event) {
        try {
            String path = "/inventory/managment/system/view/InventoryList.fxml";
            URL resource = getClass().getResource(path);
            if (resource == null) resource = getClass().getResource("/inventory/managment/system/InventoryList.fxml");
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InventoryListController controller = loader.getController();
            controller.setUserRole("DATA_ENTRY");
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inventory List");
            stage.show();
        } catch (Exception ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void logoutHandler(ActionEvent event) {
        AuthService.logout();
        NavigationManager.navigate(event, "LoginPage.fxml", "Login");
    }
}

