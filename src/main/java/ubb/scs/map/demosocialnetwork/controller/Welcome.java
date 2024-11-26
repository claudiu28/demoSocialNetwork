package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;

import java.io.IOException;

public class Welcome {
    @FXML
    private Button LoginButton;
    @FXML
    private Button SignUpButton;
    @FXML
    private Button Close;

    public Welcome() {
    }

    @FXML
    void CloseApp() {
        Stage stage = (Stage) Close.getScene().getWindow();
        stage.close();
    }

    @FXML
    void goToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Welcome.class.getResource("/ubb/scs/map/demosocialnetwork/LoginPage.fxml"));
            fxmlLoader.setControllerFactory(controller -> new LoginPage());
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Login Failed");
        }
    }

    @FXML
    void goToSignUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Welcome.class.getResource("/ubb/scs/map/demosocialnetwork/SignUpPage.fxml"));
            fxmlLoader.setControllerFactory(controller -> new SignUpPage());
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Sign Up Failed");
        }
    }
}