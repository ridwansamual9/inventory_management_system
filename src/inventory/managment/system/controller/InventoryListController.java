package inventory.managment.system.controller;

import inventory.managment.system.dao.DAOFactory;
import inventory.managment.system.dao.ItemDAO;
import inventory.managment.system.model.InventoryDisplayItem;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.service.RequestService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the Inventory List View.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class InventoryListController implements Initializable {

    @FXML
    private Button homebtn;
    @FXML
    private Button searchbtn;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private ComboBox<String> brandTypeCombo;
    @FXML
    private ComboBox<String> conditionCombo;
    @FXML
    private ComboBox<String> searchItemTypeCombo;
    @FXML
    private Label itemType;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<InventoryDisplayItem> inventoryTable;
    @FXML
    private TableColumn<InventoryDisplayItem, String> itemTypeColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, String> serialNumberColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, String> brandColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, String> modelColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, String> conditionColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, String> addDateColumn;
    @FXML
    private TableColumn<InventoryDisplayItem, Void> actionsColumn;

    private final ItemDAO itemDAO = DAOFactory.getItemDAO();
    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    
    private String userRole;
    private ObservableList<InventoryDisplayItem> allItems;
    private FilteredList<InventoryDisplayItem> filteredItems;

    public void setUserRole(String role) {
        this.userRole = role;
        if (filteredItems == null) {
            loadItems();
        }
    }
    
    private void loadItems() {
        try {
            List<InventoryDisplayItem> items = itemDAO.getAllItems();
            allItems = FXCollections.observableArrayList(items);
            filteredItems = new FilteredList<>(allItems, p -> true);
            inventoryTable.setItems(filteredItems);
            
            boolean isDataEntry = "DATA_ENTRY".equalsIgnoreCase(userRole);
            actionsColumn.setVisible(isDataEntry);
            addDateColumn.setVisible(!isDataEntry);
            
            if (isDataEntry) {
                setupActionsColumn();
            }
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
            showAlert("Error", "Could not load inventory: " + ex.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) {
            userRole = authService.getCurrentUser().getRole();
        }

        // Setup UI Components
        searchTypeCombo.getItems().addAll("All", "SERIAL_NUMBER", "MODEL", "BRAND");
        searchTypeCombo.setValue("All");
        brandTypeCombo.getItems().addAll("Dell", "HP", "Lenovo", "Apple", "Asus", "Acer", "Microsoft");
        conditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        searchItemTypeCombo.getItems().addAll("Laptop", "Desktop", "Accessory");
        
        setupTable();
        updateSearchUI();
        
        searchTypeCombo.setOnAction(e -> updateSearchUI());
        if (userRole != null) loadItems();
    }

    private void setupTable() {
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        addDateColumn.setCellValueFactory(new PropertyValueFactory<>("addDate"));
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                hbox.getChildren().addAll(editBtn, deleteBtn);
                editBtn.setOnAction(event -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void updateSearchUI() {
        boolean isAll = "All".equals(searchTypeCombo.getValue());
        searchTextField.setDisable(isAll);
        brandTypeCombo.setDisable(isAll);
        conditionCombo.setDisable(isAll);
        searchItemTypeCombo.setDisable(isAll);
    }    

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        if ("DATA_ENTRY".equalsIgnoreCase(userRole)) {
            NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
        } else {
            NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
        }
    }
    
    @FXML
    private void searchbtnHundlear(ActionEvent event) {
        applySearch();
    }
    
    private void applySearch() {
        if (filteredItems == null) return;
        
        String searchType = searchTypeCombo.getValue();
        if ("All".equals(searchType)) {
            filteredItems.setPredicate(item -> true);
            return;
        }
        
        String text = searchTextField.getText().trim().toLowerCase();
        String typeF = searchItemTypeCombo.getValue();
        String brandF = brandTypeCombo.getValue();
        String condF = conditionCombo.getValue();
        
        filteredItems.setPredicate(item -> {
            boolean matches = true;
            if (!text.isEmpty()) {
                if ("SERIAL_NUMBER".equals(searchType)) matches &= item.getSerialNumber().toLowerCase().contains(text);
                else if ("MODEL".equals(searchType)) matches &= item.getModel().toLowerCase().contains(text);
                else if ("BRAND".equals(searchType)) matches &= item.getBrand().toLowerCase().contains(text);
            }
            if (typeF != null) matches &= item.getItemType().equalsIgnoreCase(typeF);
            if (brandF != null) matches &= item.getBrand().toLowerCase().contains(brandF.toLowerCase());
            if (condF != null) matches &= item.getCondition().equalsIgnoreCase(condF);
            return matches;
        });
    }

    private void handleEdit(InventoryDisplayItem item) {
        try {
            int userId = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getId() : 1;
            requestService.submitUpdateRequest(item.getItemType().toUpperCase(), item.getSerialNumber(), userId);
            showAlert("Success", "Edit request submitted for approval.");
        } catch (SQLException ex) {
            showAlert("Error", "Submission failed: " + ex.getMessage());
        }
    }
    
    private void handleDelete(InventoryDisplayItem item) {
        try {
            int userId = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getId() : 1;
            requestService.submitDeleteRequest(item.getItemType().toUpperCase(), item.getSerialNumber(), userId);
            showAlert("Success", "Delete request submitted for approval.");
        } catch (SQLException ex) {
            showAlert("Error", "Submission failed: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showWait();
    }
    
    public void setSearchParameters(String searchText, String searchType) {
        this.searchTextField.setText(searchText);
        this.searchTypeCombo.setValue(searchType);
        updateSearchUI();
        applySearch();
    }
}

