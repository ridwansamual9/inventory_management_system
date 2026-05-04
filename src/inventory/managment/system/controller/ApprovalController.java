package inventory.managment.system.controller;

import inventory.managment.system.model.Request;
import inventory.managment.system.service.AuthService;
import inventory.managment.system.service.RequestService;
import inventory.managment.system.util.NavigationManager;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.text.SimpleDateFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * Controller for the Approval Management screen.
 * Strictly follows MVC pattern and uses Singleton services.
 */
public class ApprovalController implements Initializable {

    @FXML private ListView<String> pendingRequestList;
    @FXML private ListView<String> requestDetailsList;
    @FXML private Button backTodashboardbtn;
    
    private final RequestService requestService = RequestService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private List<Request> pendingRequests;
    private Request selectedRequest;
    private int currentManagerUserId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authService.getCurrentUser() != null) {
            currentManagerUserId = authService.getCurrentUser().getId();
        }
        loadPendingRequests();
        
        pendingRequestList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int index = pendingRequestList.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < pendingRequests.size()) {
                    selectedRequest = pendingRequests.get(index);
                    loadRequestDetails(selectedRequest);
                }
            }
        });
    }

    private void loadPendingRequests() {
        try {
            pendingRequests = requestService.getPendingRequests();
            ObservableList<String> requestStrings = FXCollections.observableArrayList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Request req : pendingRequests) {
                requestStrings.add(String.format("Request #%d: %s - %s (%s)", 
                    req.getRequestId(), req.getRequestType(), req.getItemType(), 
                    req.getRequestDate() != null ? sdf.format(req.getRequestDate()) : "N/A"));
            }
            pendingRequestList.setItems(requestStrings);
        } catch (SQLException ex) {
            showAlert("Error", "Load failed: " + ex.getMessage());
        }
    }
    
    private void loadRequestDetails(Request request) {
        try {
            Request details = requestService.getRequestDetails(request.getRequestId());
            ObservableList<String> detailStrings = FXCollections.observableArrayList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            detailStrings.add("Request ID: " + details.getRequestId());
            detailStrings.add("Request Type: " + details.getRequestType());
            detailStrings.add("Item Type: " + details.getItemType());
            detailStrings.add("Item ID: " + details.getItemId());
            detailStrings.add("Status: " + details.getStatus());
            if (details.getRequestDate() != null) detailStrings.add("Date: " + sdf.format(details.getRequestDate()));
            if (details.getUsername() != null) detailStrings.add("Requested by: " + details.getUsername());
            
            // Item details
            if (details.getTempSerialNumber() != null || details.getTempTowerSerialNumber() != null || details.getTempAccessorySerialNumber() != null) {
                detailStrings.add("");
                detailStrings.add("--- Modification Data ---");
                if ("LAPTOP".equals(details.getItemType())) {
                    detailStrings.add("Serial: " + details.getTempSerialNumber());
                    detailStrings.add("Brand: " + details.getTempBrand());
                    detailStrings.add("Model: " + details.getTempModel());
                    detailStrings.add("Condition: " + details.getTempCondition());
                } else if ("DESKTOP".equals(details.getItemType())) {
                    detailStrings.add("Serial: " + details.getTempTowerSerialNumber());
                    detailStrings.add("Model: " + details.getTempTowerModel());
                    detailStrings.add("Condition: " + details.getTempDesktopCondition());
                } else if ("ACCESSORY".equals(details.getItemType())) {
                    detailStrings.add("Type: " + details.getTempAccessoryType());
                    detailStrings.add("Brand: " + details.getTempAccessoryBrand());
                    detailStrings.add("Model: " + details.getTempAccessoryModel());
                }
            }
            requestDetailsList.setItems(detailStrings);
        } catch (SQLException ex) {
            showAlert("Error", "Details failed: " + ex.getMessage());
        }
    }

    @FXML
    private void backToDashboardHandler(ActionEvent event) {
        NavigationManager.navigate(event, "ManagerDashboard.fxml", "Manager Dashboard");
    }
    
    @FXML
    private void approveRequest() {
        if (selectedRequest == null) return;
        try {
            requestService.approveRequest(selectedRequest.getRequestId(), currentManagerUserId);
            showAlert("Success", "Request approved!");
            loadPendingRequests();
            requestDetailsList.getItems().clear();
            selectedRequest = null;
        } catch (SQLException ex) {
            showAlert("Error", "Approval failed: " + ex.getMessage());
        }
    }
    
    @FXML
    private void rejectRequest() {
        if (selectedRequest == null) return;
        try {
            requestService.rejectRequest(selectedRequest.getRequestId(), currentManagerUserId);
            showAlert("Success", "Request rejected.");
            loadPendingRequests();
            requestDetailsList.getItems().clear();
            selectedRequest = null;
        } catch (SQLException ex) {
            showAlert("Error", "Rejection failed: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

