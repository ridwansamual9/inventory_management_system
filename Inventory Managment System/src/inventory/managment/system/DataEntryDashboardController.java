/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package inventory.managment.system;
//
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.util.Arrays;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox; 
import javafx.scene.layout.GridPane; 
import javafx.scene.layout.AnchorPane; 
import javafx.scene.control.Label; 
import javafx.scene.control.Button;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import inventory.managment.system.InventoryListController;

/**
 * FXML Controller class
 *
 * @author REDWAN
 */
public class DataEntryDashboardController implements Initializable {

    @FXML
    private AnchorPane requstG1;
    @FXML
    private AnchorPane requstG2;
    @FXML
    private AnchorPane requstG3;
    @FXML
    private Button allRequstViewbtn;
    @FXML
    private AnchorPane recentlyp1;
    @FXML
    private AnchorPane recentlyp2;
    @FXML
    private AnchorPane recentlyp3;

    @FXML
    private void requstViewHundlear(ActionEvent event) {
    }
private enum Searchby{SERIAL_NUMBER,MODEL,BRAND};
        
    @FXML
    private Button inventorybtn;
    @FXML
    private Button addItem;
    @FXML
    private Button updatebtn;
    @FXML
    private Button delatebtn;
    @FXML
    private Button myRequstbtn;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextField shearchfield;
    @FXML
    private Button searchbtn;
    @FXML
    private ComboBox<Searchby> searchcomb;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate combo box
        searchcomb.getItems().addAll(Arrays.asList(Searchby.values()));

        // Populate requests panes
        List<String> requests = Databaseconnection.getRecentRequests();
        AnchorPane[] requestPanes = {requstG1, requstG2, requstG3};
        for (int i = 0; i < requestPanes.length; i++) {
            if (i < requests.size()) {
                Label label = new Label(requests.get(i));
                label.setWrapText(true);
                requestPanes[i].getChildren().add(label);
                AnchorPane.setTopAnchor(label, 10.0);
                AnchorPane.setLeftAnchor(label, 10.0);
                AnchorPane.setRightAnchor(label, 10.0);
                requestPanes[i].setVisible(true);
            } else {
                requestPanes[i].setVisible(false);
            }
        }

        // Populate recent items panes
        List<String> recentItems = Databaseconnection.getRecentItems();
        AnchorPane[] itemPanes = {recentlyp1, recentlyp2, recentlyp3};
        for (int i = 0; i < itemPanes.length; i++) {
            if (i < recentItems.size()) {
                Label label = new Label(recentItems.get(i));
                label.setWrapText(true);
                itemPanes[i].getChildren().add(label);
                AnchorPane.setTopAnchor(label, 10.0);
                AnchorPane.setLeftAnchor(label, 10.0);
                AnchorPane.setRightAnchor(label, 10.0);
                itemPanes[i].setVisible(true);
            } else {
                itemPanes[i].setVisible(false);
            }
        }

        // Update statistics
        totalPending.setText(String.valueOf(Databaseconnection.getTotalPendingRequests()));
        totalAppr.setText(String.valueOf(Databaseconnection.getTotalApprovedRequests()));
        totalR.setText(String.valueOf(Databaseconnection.getTotalRejectedRequests()));
        totalItemn.setText(String.valueOf(Databaseconnection.getTotalItems()));
    }    


    @FXML
    private void delateHundlear(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DelateItem.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void myRequstHundlear(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MyRequsts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchHuldlear(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryList.fxml"));
            Parent root = loader.load();
            InventoryListController controller = loader.getController();
            
            // Get search parameters from current fields
            String searchText = shearchfield.getText().trim();
            String searchType = searchcomb.getValue() != null ? searchcomb.getValue().toString() : "SERIAL_NUMBER";
            
            // Set role for data entry (can edit/delete)
            controller.setUserRole("data_entry");
            
            // Apply search parameters
            controller.setSearchParameters(searchText, searchType);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void searchCombHundlear(ActionEvent event) {
       
         // Combo box is populated in initialize
        
    }

    @FXML
    private void homeBtnHundler(ActionEvent event) {
    }

    @FXML
    private void addItemHundler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddItem.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void updatehundlear(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateItem.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void inventoryBtnHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryList.fxml"));
            Parent root = loader.load();
            InventoryListController controller = loader.getController();
            controller.setUserRole("data_entry");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void logoutHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Loginpage.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    
}
