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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Date;

/**
 * FXML Controller class
 *
 * @author REDWAN
 */
public class MyRequestsController implements Initializable {

    @FXML
    private TableView<Request> requestsTable;
    @FXML
    private TableColumn<Request, Integer> idColumn;
    @FXML
    private TableColumn<Request, String> typeColumn;
    @FXML
    private TableColumn<Request, String> itemNameColumn;
    @FXML
    private TableColumn<Request, String> itemTypeColumn;
    @FXML
    private TableColumn<Request, String> statusColumn;
    @FXML
    private TableColumn<Request, Date> dateColumn;
    @FXML
    private TableColumn<Request, Void> actionColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Add button to action column
        addButtonToTable();

        // Load data
        loadRequests();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Request, Void>, TableCell<Request, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Request, Void> call(final TableColumn<Request, Void> param) {
                final TableCell<Request, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Cancel");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Request request = getTableView().getItems().get(getIndex());
                            cancelRequest(request.getId());
                            loadRequests(); // Refresh table
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void loadRequests() {
        ObservableList<Request> requests = FXCollections.observableArrayList();
        String query = "SELECT id, type, item_name, item_type, status, date FROM requests"; // Adjust columns as needed
        try (Connection con = Databaseconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(new Request(rs.getInt("id"), rs.getString("type"), rs.getString("item_name"), rs.getString("item_type"), rs.getString("status"), rs.getDate("date")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        requestsTable.setItems(requests);
    }

    private void cancelRequest(int id) {
        String query = "DELETE FROM requests WHERE id = ?";
        try (Connection con = Databaseconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Inner class for Request model
    public static class Request {
        private int id;
        private String type;
        private String itemName;
        private String itemType;
        private String status;
        private Date date;

        public Request(int id, String type, String itemName, String itemType, String status, Date date) {
            this.id = id;
            this.type = type;
            this.itemName = itemName;
            this.itemType = itemType;
            this.status = status;
            this.date = date;
        }

        public int getId() { return id; }
        public String getType() { return type; }
        public String getItemName() { return itemName; }
        public String getItemType() { return itemType; }
        public String getStatus() { return status; }
        public Date getDate() { return date; }
    }
}