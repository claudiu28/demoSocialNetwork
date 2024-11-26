package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;

import java.io.IOException;

public abstract class BaseController {

    protected void openHomeWindow(Stage homeStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/UsersHome.fxml"));
            loader.setControllerFactory(context -> new UsersHome(homeStage));

            Scene scene = new Scene(loader.load());
            homeStage.resizableProperty().setValue(false);

            homeStage.setScene(scene);

            homeStage.show();
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Home window", "Something went wrong");
        }
    }
}