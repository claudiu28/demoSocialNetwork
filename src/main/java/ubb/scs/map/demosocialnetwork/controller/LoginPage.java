package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;

import java.util.Optional;

public class LoginPage extends BaseController {
    private ServiceAuthentication serviceAuthentication;

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
        try {
            this.serviceAuthentication = ApplicationContext.getInstance().getServiceAuthentication();

        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Service initialization failed!");
        }
    }

    @FXML
    public void initialize() {
        LoginButton.setOnAction(event -> loginUser());
    }

    @FXML
    public void goToWelcome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
            fxmlLoader.setControllerFactory(context -> new Welcome());
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
            fxmlLoader.setControllerFactory(context -> new SignUpPage());
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
            Stage homeStage = new Stage();

            Optional<User> loggedUser = serviceAuthentication.login(username, email, password, homeStage);
            if (loggedUser.isPresent()) {
                homeStage.setTitle("Home" + " - " + username);
                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful login!", "Welcome");
                openHomeWindow(homeStage);

                this.password.clear();
                this.username.clear();
                this.email.clear();
            } else {
                AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Check fields!", "User not exists!");
            }
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Login failed");
        }
    }

    public void ValidationFields(String username, String password, String email) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Please fill all fields");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password should have at least 6 characters");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Please enter a valid email");
        }
    }
}
