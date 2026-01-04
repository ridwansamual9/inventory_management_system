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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author REDWAN
 */
public class AddItemController implements Initializable {

    @FXML
    private Label laptopSRtxt;
    @FXML
    private Label laptopGtxt;
    @FXML
    private ComboBox<?> laptopBranComo;
    @FXML
    private Label laptopBrandtxt;
    @FXML
    private Label laptopModeltxt;
    @FXML
    private Label laptopCtxt;
    @FXML
    private TextField laptopSNtextfield;
    @FXML
    private ComboBox<?> laptopconditionCombo;
    @FXML
    private RadioButton laptopHMYesT;
    @FXML
    private RadioButton laptopHMNoT;
    @FXML
    private TextField laptopGtextField;
    @FXML
    private TextField laptopModeltextField;
    @FXML
    private RadioButton laptopHCYesT;
    @FXML
    private RadioButton laptopHCNoT;
    @FXML
    private Label laptopHCtxt;
    @FXML
    private Label laptopHMtxt;
    @FXML
    private Button laptopcancelAddbtn;
    @FXML
    private Button laptopSaveAddbtn;
    @FXML
    private Label dTowerSNtxt;
    @FXML
    private Label dTowerGtxt;
    @FXML
    private Label dMonitortxt;
    @FXML
    private Label dTowerMtxt;
    @FXML
    private Label dConditiontxt;
    @FXML
    private TextField dTowerSNtextField;
    @FXML
    private ComboBox<?> dConditionCombo;
    @FXML
    private RadioButton dHaveMToggleYesbtn;
    @FXML
    private RadioButton dHaveMToggleNobtn;
    @FXML
    private TextField dTowerGNtextField;
    @FXML
    private TextField dTowerModeltextField;
    @FXML
    private Label dHaveMtxt;
    @FXML
    private Label dKeyboardtxt;
    @FXML
    private TextField dMonitorTextField;
    @FXML
    private TextField dKeyboeardtextField;
    @FXML
    private Button dexktopCancelAddbtn;
    @FXML
    private Button dektopSaveAddbtn;
    @FXML
    private Label accessoryTaypetxt;
    @FXML
    private ComboBox<?> accessoryTaypCombo;
    @FXML
    private Label accessoryBrantxt;
    @FXML
    private Label accessoryModletxt;
    @FXML
    private Label accessoryCondtxt;
    @FXML
    private TextField accessoryBrandtexfield;
    @FXML
    private TextField accessoryModeltextField;
    @FXML
    private Label accessorySNtxt;
    @FXML
    private TextField accessorySNtextField;
    @FXML
    private ImageView accessoryImage;
    @FXML
    private Button accessorycancelAddbtn;
    @FXML
    private Button accessorySaveAddbtn;
    @FXML
    private ComboBox<?> accessoryCondCombo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void laptopcancelAddbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void laptopSaveAddbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void dexktopCancelAddbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void dektopSaveAddbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void accessorycancelAddbtnHundlear(ActionEvent event) {
    }

    @FXML
    private void accessorySaveAddbtnHundlear(ActionEvent event) {
    }
    
}
