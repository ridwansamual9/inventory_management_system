package inventory.managment.system.controller;

import inventory.managment.system.model.Request;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.service.RequestService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the My Requests screen.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class MyRequestsController implements Initializable {

    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, Integer> idColumn;
    @FXML private TableColumn<Request, String> typeColumn;
    @FXML private TableColumn<Request, String> itemNameColumn;
    @FXML private TableColumn<Request, String> itemTypeColumn;
    @FXML private TableColumn<Request, String> statusColumn;
    @FXML private TableColumn<Request, Timestamp> dateColumn;
    @FXML private TableColumn<Request, Void> actionColumn;
    @FXML private Button homebtn;

    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private int currentUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getId();
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        setupActions();
        loadRequests();
    }

    private void setupActions() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Cancel");
            {
                btn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    if ("PENDING".equals(request.getStatus())) {
                        try {
                            requestService.cancelRequest(request.getRequestId());
                            loadRequests();
                        } catch (SQLException ex) {
                            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request request = getTableView().getItems().get(getIndex());
                    boolean isPending = "PENDING".equals(request.getStatus());
                    btn.setDisable(!isPending);
                    btn.setText(isPending ? "Cancel" : "N/A");
                    setGraphic(btn);
                }
            }
        });
    }

    private void loadRequests() {
        try {
            List<Request> userRequests = requestService.getRequestsByUserId(currentUserId);
            requestsTable.setItems(FXCollections.observableArrayList(userRequests));
        } catch (SQLException ex) {
            inventory.managment.system.service.AlertService.getInstance().showError("System Error", ex.getMessage());
        }
    }

    @FXML
    private void homebtnHundlear(ActionEvent event) {
        NavigationManager.navigate(event, "DataEntryDashboard.fxml", "Data Entry Dashboard");
    }
}

