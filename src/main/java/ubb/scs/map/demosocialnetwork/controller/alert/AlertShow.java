package ubb.scs.map.demosocialnetwork.controller.alert;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertShow {
    public static void showAlert(Alert.AlertType type,String text, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(text);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
