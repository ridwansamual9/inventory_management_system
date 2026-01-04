/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package inventory.managment.system;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author REDWAN
 */
public class DelateItemController implements Initializable {

    @FXML
    private Button homebtn;
    @FXML
    private TextField shearchtextField;
    @FXML
    private Button searchbtn;
    @FXML
    private ListView<?> inventoryList;
    @FXML
    private Button delatebtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void homebtnHundlear(ActionEvent event) {
    }

    @FXML
    private void searchbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void deletebtnHundlear(ActionEvent event) {
    }
    
}
