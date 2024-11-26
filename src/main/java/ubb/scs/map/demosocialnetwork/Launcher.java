package ubb.scs.map.demosocialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.Welcome;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;

import java.util.Objects;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
            fxmlLoader.setControllerFactory(param -> new Welcome());
            Scene scene = new Scene(fxmlLoader.load(), 1106, 447);

            stage.setTitle("Social Media Application");
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/assets/imag3.jpg")).toString()));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "App failed to start!");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
