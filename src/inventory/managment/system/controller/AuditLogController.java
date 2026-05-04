package inventory.managment.system.controller;

import inventory.managment.system.dao.DAOFactory;
import inventory.managment.system.dao.AuditDAO;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 * Controller for the Audit Log screen.
 * Strictly follows MVC pattern and uses DAOFactory.
 */
public class AuditLogController implements Initializable {

    @FXML private Button homebtn;
    @FXML private ComboBox<String> actionFilterCombo;
    @FXML private ListView<String> auditList;

    private final AuditDAO auditDAO = DAOFactory.getAuditDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actionFilterCombo.getItems().addAll("All Actions", "ADD_ITEM", "UPDATE", "DELETE");
        actionFilterCombo.setValue("All Actions");
        
        loadAuditLogs("All Actions");
        
        actionFilterCombo.setOnAction(e -> {
            String filter = actionFilterCombo.getValue();
            if (filter != null) loadAuditLogs(filter);
        });
    }

    private void loadAuditLogs(String filter) {
        try {
            auditList.setItems(FXCollections.observableArrayList(auditDAO.getAuditLogs(filter)));
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
    }
}

