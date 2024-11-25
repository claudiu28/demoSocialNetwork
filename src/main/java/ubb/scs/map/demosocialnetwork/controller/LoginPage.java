package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.factory.ServiceAuthFactory;
import ubb.scs.map.demosocialnetwork.service.CurrentUserSession;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;

import java.util.Optional;

public class LoginPage {
    private final ServiceAuthentication serviceAuthentication;

    @FXML
    private Button LoginButton;
    @FXML
    private Button SignUpButton;
    @FXML
    private Button toWelcome;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField email;

    public LoginPage() {
        this.serviceAuthentication = ServiceAuthFactory.getInstance();
    }

    @FXML
    public void goToWelcome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) toWelcome.getScene().getWindow();
            stage.resizableProperty().setValue(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    @FXML
    public void goToSignUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/SignUpPage.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) SignUpButton.getScene().getWindow();
            stage.resizableProperty().setValue(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    @FXML
    public void loginUser() {
        String username = this.username.getText();
        String password = this.password.getText();
        String email = this.email.getText();

        try {
            ValidationFields(username, password, email);
            Optional<User> loggedUser = serviceAuthentication.login(username, email, password);
            if (loggedUser.isPresent()) {
                CurrentUserSession.getInstance().setCurrentUser(loggedUser.get());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/UsersHome.fxml"));
                UsersHome UsersHome = loader.getController();

                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful login!", "Welcome");

                Scene scene = new Scene(loader.load());
                javafx.stage.Stage stage = (javafx.stage.Stage) LoginButton.getScene().getWindow();
                stage.resizableProperty().setValue(false);
                stage.setOnCloseRequest(event -> UsersHome.onClose());
                stage.setScene(scene);
                stage.show();
            } else {
                AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "User does not exist!", "User not exists!");
            }
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    public static void ValidationFields(String username, String password, String email) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Please fill all the fields");
            return;
        }
        if (password.length() < 6) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Password must be at least 6 characters");
            return;
        }
        if (!email.contains("@")) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Please enter a valid email");
        }
    }
}
