package inventory.managment.system.controller;

import inventory.managment.system.dao.DAOFactory;
import inventory.managment.system.dao.ItemDAO;
import inventory.managment.system.model.InventoryDisplayItem;
import inventory.managment.system.util.NavigationManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the Report Generation screen.
 * Strictly follows MVC pattern and uses DAOFactory.
 */
public class ReportController implements Initializable {

    @FXML private ComboBox<String> itemtaypeCombo;
    @FXML private ComboBox<String> brandCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private Button backToDachboarbtn;
    @FXML private ListView<String> reportResultList;

    private final ItemDAO itemDAO = DAOFactory.getItemDAO();
    private List<InventoryDisplayItem> currentReportItems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemtaypeCombo.getItems().addAll("All Item", "Laptop", "Desktop", "Accessory");
        itemtaypeCombo.setValue("All Item");
        conditionCombo.getItems().addAll("All Condition", "NEW", "GOOD", "FAIR", "POOR");
        conditionCombo.setValue("All Condition");
        brandCombo.getItems().add("All Brand");
        brandCombo.setValue("All Brand");

        itemtaypeCombo.setOnAction(e -> updateBrands());
        updateBrands();
    }

    private void updateBrands() {
        try {
            String type = itemtaypeCombo.getValue();
            List<String> brands = itemDAO.getAllBrands(type);
            brandCombo.getItems().clear();
            brandCombo.getItems().add("All Brand");
            brandCombo.getItems().addAll(brands);
            brandCombo.setValue("All Brand");
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void backToDashboardHandler(ActionEvent event) {
        NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
    }

    @FXML
    private void generateReportbtnHundlear(ActionEvent event) {
        try {
            String type = "All Item".equals(itemtaypeCombo.getValue()) ? null : itemtaypeCombo.getValue();
            String brand = "All Brand".equals(brandCombo.getValue()) ? null : brandCombo.getValue();
            String cond = "All Condition".equals(conditionCombo.getValue()) ? null : conditionCombo.getValue();

            currentReportItems = itemDAO.getFilteredItems(type, brand, cond);
            ObservableList<String> list = FXCollections.observableArrayList();

            if (currentReportItems.isEmpty()) {
                list.add("No items found.");
            } else {
                list.add("=== INVENTORY REPORT ===");
                list.add("Total: " + currentReportItems.size());
                list.add("");
                for (InventoryDisplayItem item : currentReportItems) {
                    list.add(String.format("%s | SN: %s | %s | %s | %s", 
                        item.getItemType(), item.getSerialNumber(), item.getBrand(), item.getModel(), item.getCondition()));
                }
            }
            reportResultList.setItems(list);
        } catch (SQLException ex) {
            showAlert("Error", "Generation failed: " + ex.getMessage());
        }
    }

    @FXML
    private void exportReportbtnHundlear(ActionEvent event) {
        if (currentReportItems == null || currentReportItems.isEmpty()) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Export Report");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("inventory_report_" + System.currentTimeMillis() + ".csv");

        File file = fc.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try (FileWriter w = new FileWriter(file)) {
                w.write("Type,SN,Brand,Model,Condition,Date\n");
                for (InventoryDisplayItem item : currentReportItems) {
                    w.write(String.format("%s,%s,%s,%s,%s,%s\n", 
                        item.getItemType(), item.getSerialNumber(), item.getBrand(), item.getModel(), item.getCondition(), item.getAddDate()));
                }
                showAlert("Success", "Exported to: " + file.getName());
            } catch (IOException ex) {
                showAlert("Error", "Export failed.");
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
}

