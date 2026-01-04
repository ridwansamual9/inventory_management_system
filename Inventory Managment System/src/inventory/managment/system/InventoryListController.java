/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package inventory.managment.system;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author REDWAN
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

    private String userRole;
    private ObservableList<InventoryDisplayItem> allItems;
    private FilteredList<InventoryDisplayItem> filteredItems;

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate search type combo
        searchTypeCombo.getItems().addAll("SERIAL_NUMBER", "MODEL", "BRAND");
        
        // Populate brand combo
        brandTypeCombo.getItems().addAll("Dell", "HP", "Lenovo", "Apple", "Asus", "Acer", "Microsoft");
        
        // Populate condition combo
        conditionCombo.getItems().addAll("NEW", "GOOD", "FAIR", "POOR");
        
        // Populate item type combo
        searchItemTypeCombo.getItems().addAll("Laptop", "Desktop", "Accessory");
        
        // Setup table columns
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        addDateColumn.setCellValueFactory(new PropertyValueFactory<>("addDate"));
        
        // Initially hide columns, will show based on role
        addDateColumn.setVisible(false);
        actionsColumn.setVisible(false);
        
        // Load items based on role
        if ("data_entry".equals(userRole)) {
            loadForDataEntry();
        } else if ("manager".equals(userRole)) {
            loadForManager();
        }
        
        // Setup search type listener
        searchTypeCombo.setOnAction(event -> updateSearchUI());
        
        // Initially set to SERIAL_NUMBER
        searchTypeCombo.setValue("SERIAL_NUMBER");
        updateSearchUI();
    }

    private void updateSearchUI() {
        String selected = searchTypeCombo.getValue();
        if ("SERIAL_NUMBER".equals(selected)) {
            searchTextField.setPromptText("Enter Serial Number");
            brandTypeCombo.setDisable(true);
            conditionCombo.setDisable(true);
            searchItemTypeCombo.setDisable(true);
        } else if ("MODEL".equals(selected)) {
            searchTextField.setPromptText("Enter Model");
            brandTypeCombo.setDisable(false);
            conditionCombo.setDisable(false);
            searchItemTypeCombo.setDisable(false);
        } else if ("BRAND".equals(selected)) {
            searchTextField.setPromptText("Enter Brand");
            brandTypeCombo.setDisable(false);
            conditionCombo.setDisable(false);
            searchItemTypeCombo.setDisable(false);
        }
    }    

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DataEntryDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadForDataEntry() {
        try {
            List<InventoryDisplayItem> items = Databaseconnection.getAllItems();
            for (InventoryDisplayItem item : items) {
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(event -> handleEdit(item));
                Button deleteBtn = new Button("Delete");
                deleteBtn.setOnAction(event -> handleDelete(item));
                item.setEditBtn(editBtn);
                item.setDeleteBtn(deleteBtn);
            }
            allItems = FXCollections.observableArrayList(items);
            filteredItems = new FilteredList<>(allItems, p -> true);
            inventoryTable.setItems(filteredItems);
            
            // Show actions column, hide add date
            actionsColumn.setVisible(true);
            addDateColumn.setVisible(false);
            actionsColumn.setCellFactory(param -> new javafx.scene.control.TableCell<InventoryDisplayItem, Void>() {
                private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                
                {
                    hbox.getChildren().addAll(editBtn, deleteBtn);
                    editBtn.setOnAction(event -> {
                        InventoryDisplayItem item = getTableView().getItems().get(getIndex());
                        handleEdit(item);
                    });
                    deleteBtn.setOnAction(event -> {
                        InventoryDisplayItem item = getTableView().getItems().get(getIndex());
                        handleDelete(item);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadForManager() {
        try {
            List<InventoryDisplayItem> items = Databaseconnection.getAllItems();
            allItems = FXCollections.observableArrayList(items);
            filteredItems = new FilteredList<>(allItems, p -> true);
            inventoryTable.setItems(filteredItems);
            
            // Show add date column, hide actions
            addDateColumn.setVisible(true);
            actionsColumn.setVisible(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchbtnHundlear(ActionEvent event) {
        String searchType = searchTypeCombo.getValue();
        String searchText = searchTextField.getText().trim().toLowerCase();
        String itemTypeFilter = searchItemTypeCombo.getValue();
        String brandFilter = brandTypeCombo.getValue();
        String conditionFilter = conditionCombo.getValue();
        
        filteredItems.setPredicate(item -> {
            boolean matches = true;
            
            // Filter by search type and text
            if ("SERIAL_NUMBER".equals(searchType)) {
                matches &= item.getSerialNumber().toLowerCase().contains(searchText);
            } else if ("MODEL".equals(searchType)) {
                matches &= item.getModel().toLowerCase().contains(searchText);
            } else if ("BRAND".equals(searchType)) {
                matches &= item.getBrand().toLowerCase().contains(searchText);
            }
            
            // Additional filters
            if (itemTypeFilter != null && !"All Items".equals(itemTypeFilter)) {
                matches &= item.getItemType().equalsIgnoreCase(itemTypeFilter);
            }
            if (brandFilter != null && !"All Brand".equals(brandFilter)) {
                matches &= item.getBrand().toLowerCase().contains(brandFilter.toLowerCase());
            }
            if (conditionFilter != null && !"All Condition".equals(conditionFilter)) {
                matches &= item.getCondition().equalsIgnoreCase(conditionFilter);
            }
            
            return matches;
        });
    }
    
    private void handleEdit(InventoryDisplayItem item) {
        try {
            String itemType = item.getItemType().toUpperCase();
            String sn = item.getSerialNumber();
            int userId = 1; // TODO: Get from session
            Databaseconnection.insertRequest("UPDATES", itemType, sn, userId);
            System.out.println("Edit request submitted for " + item.getDescription());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void handleDelete(InventoryDisplayItem item) {
        try {
            String itemType = item.getItemType().toUpperCase();
            String sn = item.getSerialNumber();
            int userId = 1; // TODO: Get from session
            Databaseconnection.insertRequest("DELETE", itemType, sn, userId);
            System.out.println("Delete request submitted for " + item.getDescription());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setSearchParameters(String searchText, String searchType) {
        this.searchTextField.setText(searchText);
        this.searchTypeCombo.setValue(searchType);
        this.searchItemTypeCombo.setValue("All Items");
        this.brandTypeCombo.setValue("All Brand");
        this.conditionCombo.setValue("All Condition");
        updateSearchUI();
        applySearch();
    }
    
    private void applySearch() {
        String searchType = searchTypeCombo.getValue();
        String searchText = searchTextField.getText().trim().toLowerCase();
        String itemTypeFilter = searchItemTypeCombo.getValue();
        String brandFilter = brandTypeCombo.getValue();
        String conditionFilter = conditionCombo.getValue();
        
        filteredItems.setPredicate(item -> {
            boolean matches = true;
            
            // Filter by search type and text
            if ("SERIAL_NUMBER".equals(searchType)) {
                matches &= item.getSerialNumber().toLowerCase().contains(searchText);
            } else if ("MODEL".equals(searchType)) {
                matches &= item.getModel().toLowerCase().contains(searchText);
            } else if ("BRAND".equals(searchType)) {
                matches &= item.getBrand().toLowerCase().contains(searchText);
            }
            
            // Additional filters
            if (itemTypeFilter != null && !"All Items".equals(itemTypeFilter)) {
                matches &= item.getItemType().equalsIgnoreCase(itemTypeFilter);
            }
            if (brandFilter != null && !"All Brand".equals(brandFilter)) {
                matches &= item.getBrand().toLowerCase().contains(brandFilter.toLowerCase());
            }
            if (conditionFilter != null && !"All Condition".equals(conditionFilter)) {
                matches &= item.getCondition().equalsIgnoreCase(conditionFilter);
            }
            
            return matches;
        });
    }
    
}
