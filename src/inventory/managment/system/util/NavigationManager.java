package inventory.managment.system.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

/**
 * NavigationManager centralizes scene switching logic to reduce boilerplate in controllers.
 */
public class NavigationManager {

    private static final String VIEW_PACKAGE = "/inventory/managment/system/view/";

    /**
     * Switches the current scene to a new FXML view.
     * @param event The ActionEvent from the button click
     * @param fxmlFileName The name of the FXML file (e.g., "LoginPage.fxml")
     * @param title The title for the new stage
     */
    public static void navigate(ActionEvent event, String fxmlFileName, String title) {
        try {
            // Note: After moving FXML to .view package, we use the full path
            String path = VIEW_PACKAGE + fxmlFileName;
            URL resource = NavigationManager.class.getResource(path);
            
            if (resource == null) {
                throw new IOException("Could not find FXML file: " + path);
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            if (title != null && !title.isEmpty()) {
                stage.setTitle(title);
            }
            stage.show();
        } catch (IOException ex) {
            System.err.println("Navigation error: " + ex.getMessage());
            ex.printStackTrace();
            // In a real app, show an alert here
        }
    }
}
