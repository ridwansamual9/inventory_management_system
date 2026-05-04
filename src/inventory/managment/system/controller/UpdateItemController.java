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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controller for the Update Item screen.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class UpdateItemController implements Initializable {

    @FXML private TextField shearchtextField;
    @FXML private Button searchbtn;
    @FXML private Tab laptopTab;
    @FXML private Tab desktopTab;
    @FXML private Tab accessoryTab;
    @FXML private Label messageLabel;

    @FXML private TextField laptopSNtextfield;
    @FXML private TextField laptopGtextField;
    @FXML private TextField laptopModeltextField;
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
    @FXML private RadioButton dHaveMToggleYesbtn;
    @FXML private RadioButton dHaveMToggleNobtn;
    @FXML private TextField dKeyboeardtextField;

    @FXML private TextField accessorySNtextField;
    @FXML private TextField accessoryBrandtexfield;
    @FXML private TextField accessoryModeltextField;
    @FXML private ComboBox<String> accessoryTaypCombo;
    @FXML private ComboBox<String> accessoryConCombo;

    private final ItemDAO itemDAO = DAOFactory.getItemDAO();
    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private Object currentItem = null;
    private int currentUserId;

    private final ToggleGroup lapC = new ToggleGroup();
    private final ToggleGroup lapM = new ToggleGroup();
    private final ToggleGroup deskM = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) currentUserId = authService.getCurrentUser().getId();

        laptopconditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        dConditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        accessoryConCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        accessoryTaypCombo.getItems().addAll("MONITOR", "TOWER", "KEYBOARD", "MOUSE");
        laptopBranComo.getItems().addAll("Apple", "Dell", "HP", "Lenovo", "Asus", "Acer", "MSI", "Samsung", "Microsoft");

        laptopHCYesT.setToggleGroup(lapC); laptopHCNoT.setToggleGroup(lapC);
        laptopHMYesT.setToggleGroup(lapM); laptopHMNoT.setToggleGroup(lapM);
        dHaveMToggleYesbtn.setToggleGroup(deskM); dHaveMToggleNobtn.setToggleGroup(deskM);

        laptopTab.setDisable(true);
        desktopTab.setDisable(true);
        accessoryTab.setDisable(true);
    }

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }

    @FXML
    private void searchbtnHundlear(ActionEvent event) {
        String sn = shearchtextField.getText().trim();
        if (sn.isEmpty()) return;

        searchbtn.setDisable(true);
        showMessage("Searching...", true);
        AtomicBoolean found = new AtomicBoolean(false);

        new Thread(() -> {
            try {
                Laptop l = itemDAO.getLaptopBySerial(sn);
                if (l != null) { finishSearch(found, l, "LAPTOP"); return; }
                Desktop d = itemDAO.getDesktopBySerial(sn);
                if (d != null) { finishSearch(found, d, "DESKTOP"); return; }
                Accessory a = itemDAO.getAccessoryBySerial(sn);
                if (a != null) { finishSearch(found, a, "ACCESSORY"); return; }
            } catch (SQLException e) {
                inventory.managment.system.service.AlertService.getInstance().showError("System Error", e.getMessage());
            } finally {
                Platform.runLater(() -> {
                    searchbtn.setDisable(false);
                    if (!found.get()) showMessage("Not found.", false);
                });
            }
        }).start();
    }

    private void finishSearch(AtomicBoolean found, Object item, String type) {
        found.set(true);
        Platform.runLater(() -> {
            currentItem = item;
            displayItem(item, type);
            showMessage(type + " found! Edit and save.", true);
        });
    }

    private void displayItem(Object item, String type) {
        laptopTab.setDisable(true); desktopTab.setDisable(true); accessoryTab.setDisable(true);
        if ("LAPTOP".equals(type)) {
            Laptop l = (Laptop) item;
            laptopSNtextfield.setText(l.getSerialNumber());
            laptopGtextField.setText(l.getGeneration());
            laptopModeltextField.setText(l.getModel());
            laptopconditionCombo.setValue(l.getItemCondition().name());
            laptopHCYesT.setSelected(l.isHaveCharger()); laptopHCNoT.setSelected(!l.isHaveCharger());
            laptopHMYesT.setSelected(l.isHaveMouse()); laptopHMNoT.setSelected(!l.isHaveMouse());
            laptopTab.setDisable(false);
            laptopTab.getTabPane().getSelectionModel().select(laptopTab);
        } else if ("DESKTOP".equals(type)) {
            Desktop d = (Desktop) item;
            dTowerSNtextField.setText(d.getTowerSerialNumber());
            dTowerGNtextField.setText(d.getTowerGeneration());
            dTowerModeltextField.setText(d.getTowerModel());
            dConditionCombo.setValue(d.getDesktopCondition().name());
            dKeyboeardtextField.setText(d.getKeyboardModel());
            dHaveMToggleYesbtn.setSelected(d.isHaveMouse()); dHaveMToggleNobtn.setSelected(!d.isHaveMouse());
            desktopTab.setDisable(false);
            desktopTab.getTabPane().getSelectionModel().select(desktopTab);
        } else if ("ACCESSORY".equals(type)) {
            Accessory a = (Accessory) item;
            accessorySNtextField.setText(a.getSerialNumber());
            accessoryBrandtexfield.setText(a.getBrand());
            accessoryModeltextField.setText(a.getModel());
            accessoryTaypCombo.setValue(a.getType().name());
            accessoryConCombo.setValue(a.getItemCondition().name());
            accessoryTab.setDisable(false);
            accessoryTab.getTabPane().getSelectionModel().select(accessoryTab);
        }
    }

    @FXML
    private void laptopSaveAddbtnHundlear(ActionEvent event) {
        try {
            Laptop l = new Laptop(laptopSNtextfield.getText(), ((Laptop)currentItem).getBrand(), laptopModeltextField.getText(), laptopGtextField.getText(), INVItem.condition.valueOf(laptopconditionCombo.getValue()), laptopHCYesT.isSelected(), laptopHMYesT.isSelected());
            requestService.submitUpdateLaptopRequest(((INVItem)currentItem).getItemId(), l, currentUserId);
            showAlert("Success", "Request submitted."); clearForm();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML private void laptopcancelAddbtnHundlear(ActionEvent event) { clearForm(); }
    @FXML private void dexktopCancelAddbtnHundlear(ActionEvent event) { clearForm(); }
    @FXML private void accessorycancelAddbtnHundlear(ActionEvent event) { clearForm(); }

    @FXML
    private void dektopSaveAddbtnHundlear(ActionEvent event) {
        try {
            Desktop d = new Desktop(dTowerSNtextField.getText(), dTowerGNtextField.getText(), dTowerModeltextField.getText(), INVItem.condition.valueOf(dConditionCombo.getValue()), dKeyboeardtextField.getText(), dHaveMToggleYesbtn.isSelected());
            requestService.submitUpdateDesktopRequest(((INVItem)currentItem).getItemId(), d, currentUserId);
            showAlert("Success", "Request submitted."); clearForm();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void accessorySaveAddbtnHundlear(ActionEvent event) {
        try {
            Accessory a = new Accessory(Accessory.type.valueOf(accessoryTaypCombo.getValue()), accessoryBrandtexfield.getText(), accessorySNtextField.getText(), accessoryModeltextField.getText(), INVItem.condition.valueOf(accessoryConCombo.getValue()));
            requestService.submitUpdateAccessoryRequest(((INVItem)currentItem).getItemId(), a, currentUserId);
            showAlert("Success", "Request submitted."); clearForm();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void clearForm() {
        shearchtextField.clear(); laptopTab.setDisable(true); desktopTab.setDisable(true); accessoryTab.setDisable(true); currentItem = null;
    }

    private void showMessage(String msg, boolean success) {
        Platform.runLater(() -> {
            if (messageLabel != null) {
                messageLabel.setText(msg);
                messageLabel.setStyle("-fx-text-fill: " + (success ? "green;" : "red;"));
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setContentText(msg); a.show();
    }
}

