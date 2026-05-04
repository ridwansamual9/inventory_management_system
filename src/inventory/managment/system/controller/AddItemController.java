package inventory.managment.system.controller;

import inventory.managment.system.model.*;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.service.RequestService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Controller for the Add Item screen.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class AddItemController implements Initializable {

    @FXML private TextField laptopSNtextfield;
    @FXML private TextField laptopGtextField;
    @FXML private TextField laptopModeltextField;
    @FXML private TextField laptopBrandtextField;
    @FXML private ComboBox<String> laptopconditionCombo;
    @FXML private ComboBox<String> laptopBranComo;
    @FXML private RadioButton laptopHMYesT;
    @FXML private RadioButton laptopHMNoT;
    @FXML private RadioButton laptopHCYesT;
    @FXML private RadioButton laptopHCNoT;

    @FXML private TextField dTowerSNtextField;
    @FXML private TextField dTowerGNtextField;
    @FXML private TextField dTowerModeltextField;
    @FXML private ComboBox<String> dConditionCombo;
    @FXML private TextField dKeyboeardtextField;
    @FXML private RadioButton dHaveMToggleYesbtn;
    @FXML private RadioButton dHaveMToggleNobtn;

    @FXML private TextField accessorySNtextField;
    @FXML private TextField accessoryBrandtexfield;
    @FXML private TextField accessoryModeltextField;
    @FXML private ComboBox<String> accessoryTaypCombo;
    @FXML private ComboBox<String> accessoryCondCombo;

    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private int currentUserId;

    private final ToggleGroup lapHaveMousT = new ToggleGroup();
    private final ToggleGroup lapHaveCharT = new ToggleGroup();
    private final ToggleGroup deskHaveMousT = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getId();
        }

        // Initialize combos
        laptopconditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        laptopBranComo.getItems().addAll("Apple", "Dell", "HP", "Lenovo", "Asus", "Acer", "MSI", "Samsung", "Microsoft");
        dConditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        accessoryCondCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        accessoryTaypCombo.getItems().addAll("MONITOR", "TOWER", "KEYBOARD", "MOUSE");

        laptopHCYesT.setToggleGroup(lapHaveCharT);
        laptopHCNoT.setToggleGroup(lapHaveCharT);
        laptopHMYesT.setToggleGroup(lapHaveMousT);
        laptopHMNoT.setToggleGroup(lapHaveMousT);
        dHaveMToggleNobtn.setToggleGroup(deskHaveMousT);
        dHaveMToggleYesbtn.setToggleGroup(deskHaveMousT);
    }

    @FXML
    private void laptopcancelAddbtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }

    @FXML
    private void laptopSaveAddbtnHundlear(ActionEvent event) {
        try {
            String sn = laptopSNtextfield.getText().trim();
            String brand = laptopBrandtextField != null ? laptopBrandtextField.getText().trim() : "";
            String gen = laptopGtextField.getText().trim();
            String model = laptopModeltextField.getText().trim();
            String condStr = laptopconditionCombo.getValue();

            if (sn.isEmpty() || gen.isEmpty() || model.isEmpty() || condStr == null) {
                showAlert("Error", "Please fill required fields.");
                return;
            }

            Laptop laptop = new Laptop(sn, brand, model, gen, INVItem.condition.valueOf(condStr), laptopHCYesT.isSelected(), laptopHMYesT.isSelected());
            requestService.submitAddLaptopRequest(laptop, currentUserId);
            showAlert("Success", "Laptop addition request submitted!");
            clearLaptopForm();
        } catch (SQLException ex) {
            showAlert("Error", "SQL Exception: " + ex.getMessage());
        }
    }

    @FXML
    private void dexktopCancelAddbtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }

    @FXML
    private void dektopSaveAddbtnHundlear(ActionEvent event) {
        try {
            String sn = dTowerSNtextField.getText().trim();
            String gen = dTowerGNtextField.getText().trim();
            String model = dTowerModeltextField.getText().trim();
            String condStr = dConditionCombo.getValue();

            if (sn.isEmpty() || gen.isEmpty() || model.isEmpty() || condStr == null) {
                showAlert("Error", "Please fill required fields.");
                return;
            }

            Desktop desktop = new Desktop(sn, gen, model, INVItem.condition.valueOf(condStr), dKeyboeardtextField.getText().trim(), dHaveMToggleYesbtn.isSelected());
            requestService.submitAddDesktopRequest(desktop, currentUserId);
            showAlert("Success", "Desktop addition request submitted!");
            clearDesktopForm();
        } catch (SQLException ex) {
            showAlert("Error", "SQL Exception: " + ex.getMessage());
        }
    }

    @FXML
    private void accessorycancelAddbtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }

    @FXML
    private void accessorySaveAddbtnHundlear(ActionEvent event) {
        try {
            String sn = accessorySNtextField.getText().trim();
            String brand = accessoryBrandtexfield.getText().trim();
            String model = accessoryModeltextField.getText().trim();
            String type = accessoryTaypCombo.getValue();
            String condStr = accessoryCondCombo.getValue();

            if (sn.isEmpty() || brand.isEmpty() || model.isEmpty() || type == null || condStr == null) {
                showAlert("Error", "Please fill required fields.");
                return;
            }

            Accessory acc = new Accessory(Accessory.type.valueOf(type), brand, sn, model, INVItem.condition.valueOf(condStr));
            requestService.submitAddAccessoryRequest(acc, currentUserId);
            showAlert("Success", "Accessory addition request submitted!");
            clearAccessoryForm();
        } catch (SQLException ex) {
            showAlert("Error", "SQL Exception: " + ex.getMessage());
        }
    }

    private void clearLaptopForm() {
        laptopSNtextfield.clear();
        if (laptopBrandtextField != null) laptopBrandtextField.clear();
        laptopGtextField.clear();
        laptopModeltextField.clear();
        laptopconditionCombo.setValue(null);
        lapHaveCharT.selectToggle(null);
        lapHaveMousT.selectToggle(null);
    }

    private void clearDesktopForm() {
        dTowerSNtextField.clear();
        dTowerGNtextField.clear();
        dTowerModeltextField.clear();
        dConditionCombo.setValue(null);
        dKeyboeardtextField.clear();
        deskHaveMousT.selectToggle(null);
    }

    private void clearAccessoryForm() {
        accessorySNtextField.clear();
        accessoryBrandtexfield.clear();
        accessoryModeltextField.clear();
        accessoryTaypCombo.setValue(null);
        accessoryCondCombo.setValue(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

