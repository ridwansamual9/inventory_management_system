package inventory.managment.system.controller;

import inventory.managment.system.service.AlertService;
import inventory.managment.system.util.NavigationManager;
import java.sql.SQLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * Controller for the Alerts View.
 * Fixed typo from AlertesController and refactored for MVC.
 */
public class AlertsController implements Initializable {

    @FXML
    private Button backToDashboardbtn;
    @FXML
    private ListView<String> stockAlertList;

    private final AlertService alertService = AlertService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAlerts();
    }

    private void loadAlerts() {
        try {
            ObservableList<String> alertList = FXCollections.observableArrayList();
            
            // Low stock
            int lowStock = alertService.getLowStockCount();
            if (lowStock > 0) alertList.add("⚠️ LOW STOCK: " + lowStock + " items below threshold.");
            
            // Modifications
            int pending = alertService.getUserActionAlertsCount();
            if (pending > 0) alertList.add("📝 PENDING ACTIONS: " + pending + " modifications waiting for approval.");
            
            // Detail
            List<String> recentMods = alertService.getRecentModifications();
            for (String mod : recentMods) {
                String prefix = "📋";
                if (mod.toLowerCase().contains("add")) prefix = "➕ ADD";
                else if (mod.toLowerCase().contains("update")) prefix = "✏️ UPDATE";
                else if (mod.toLowerCase().contains("delete")) prefix = "🗑️ DELETE";
                alertList.add(prefix + ": " + mod);
            }
            
            if (alertList.isEmpty()) alertList.add("✅ System Normal. No alerts.");
            stockAlertList.setItems(alertList);
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void backToDashboardHandler(ActionEvent event) {
        NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
    }
}

