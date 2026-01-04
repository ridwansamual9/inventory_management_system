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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author REDWAN
 */
public class ReportController implements Initializable {

    @FXML
    private Button backToDachboarbtn;
    @FXML
    private ComboBox<?> itemtaypeCombo;
    @FXML
    private ComboBox<?> brandCombo;
    @FXML
    private ComboBox<?> conditionCombo;
    @FXML
    private Button generateReportbtn;
    @FXML
    private Button exportReportbtn;
    @FXML
    private ListView<?> reportResultList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void backToDachboarbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void generateReportbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void exportReportbtnHundlear(ActionEvent event) {
    }
    
}
