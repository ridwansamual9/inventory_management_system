package inventory.managment.system.controller;

import inventory.managment.system.service.AlertService;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Controller for the Manager Dashboard.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class ManagerDashboardController implements Initializable {

    @FXML
    private Button reportbtn;
    @FXML
    private Button alertbtn;
    @FXML
    private Button approvalbtn;
    @FXML
    private Button aduitlogbtn;
    @FXML
    private Button btnmanageUserbtn;
    @FXML
    private Button logoutbtn;
    @FXML
    private Button inventorybtn;
    @FXML
    private Label pendingApprovalNtxt;
    @FXML
    private Label lowStockNtxt;
    @FXML
    private Label totalItemNtxt;
    @FXML
    private Label allDataentryNtxt;
    @FXML
    private ListView<String> pendingApprovalList;
    @FXML
    private ListView<String> lowStockList;
    @FXML
    private PieChart itemDistributionChart;

    private final AlertService alertService = AlertService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            // Update statistics
            totalItemNtxt.setText(String.valueOf(alertService.getTotalItems()));
            
            int pendingCount = alertService.getTotalPendingRequests();
            pendingApprovalNtxt.setText(String.valueOf(pendingCount));
            pendingApprovalNtxt.setStyle(pendingCount > 0 ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: black;");
            
            int lowStockCount = alertService.getLowStockCount();
            lowStockNtxt.setText(String.valueOf(lowStockCount));
            lowStockNtxt.setStyle(lowStockCount > 0 ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: black;");
            
            allDataentryNtxt.setText(String.valueOf(alertService.getActiveUsersCount()));
            
            // Populate lists
            pendingApprovalList.setItems(FXCollections.observableArrayList(alertService.getRecentRequests()));
            lowStockList.setItems(FXCollections.observableArrayList(alertService.getRecentItems()));
            
            populateDistributionChart();
            updateAlertButtonIndicator();
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }
    
    private void populateDistributionChart() throws SQLException {
        itemDistributionChart.setData(FXCollections.observableArrayList(
            new PieChart.Data("Laptops", alertService.getLaptopCount()),
            new PieChart.Data("Desktops", alertService.getDesktopCount()),
            new PieChart.Data("Accessories", alertService.getAccessoryCount())
        ));
    }
    
    private void updateAlertButtonIndicator() throws SQLException {
        int lowStockCount = alertService.getLowStockCount();
        int userActionAlerts = alertService.getUserActionAlertsCount();
        boolean hasAlerts = (lowStockCount > 0) || (userActionAlerts > 0);
        
        if (hasAlerts) {
            alertbtn.setStyle("-fx-background-color: DODGERBLUE; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
            if (!alertbtn.getText().contains("●")) alertbtn.setText("Alerts ●");
        } else {
            alertbtn.setStyle("-fx-background-color: DODGERBLUE; -fx-text-fill: white; -fx-font-weight: bold;");
            alertbtn.setText("Alerts");
        }
    }

    @FXML
    private void approvalHandler(ActionEvent event) {
        NavigationManager.navigate(event, "Approval.fxml", "Approval Management");
    }

    @FXML
    private void alertHandler(ActionEvent event) {
        NavigationManager.navigate(event, "Alerts.fxml", "System Alerts");
    }

    @FXML
    private void reportHandler(ActionEvent event) {
        NavigationManager.navigate(event, "Report.fxml", "Inventory Reports");
    }

    @FXML
    private void auditLogHandler(ActionEvent event) {
        NavigationManager.navigate(event, "AuditLog.fxml", "Audit Logs");
    }

    @FXML
    private void manageUserbtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "UserManagement.fxml", "User Management");
    }

    @FXML
    private void inventoryBtnHandler(ActionEvent event) {
        try {
            // Special case as we need to set role on controller
            String path = "/inventory/managment/system/view/InventoryList.fxml";
            URL resource = getClass().getResource(path);
            if (resource == null) resource = getClass().getResource("/inventory/managment/system/InventoryList.fxml");
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InventoryListController controller = loader.getController();
            controller.setUserRole("MANAGER");
            
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

