package ubb.scs.map.demosocialnetwork;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import java.io.IOException;
import java.util.Objects;

public class Welcome extends Application {
    @FXML
    private Button LoginButton;
    @FXML
    private Button SignUpButton;
    @FXML
    private Button Close;


    @FXML
    void CloseApp() {
        Stage stage = (Stage) Close.getScene().getWindow();
        stage.close();
    }

    @FXML
    void goToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Welcome.class.getResource("/ubb/scs/map/demosocialnetwork/LoginPage.fxml"));
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
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Sign Up Failed");
        }
    }


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Welcome.class.getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1106, 447);
        stage.setTitle("Social Media Application");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(Welcome.class.getResource("/assets/imag3.jpg")).toString()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}