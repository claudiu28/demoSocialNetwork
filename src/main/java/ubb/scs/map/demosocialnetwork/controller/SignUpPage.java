package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.ValidationException;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;


import java.io.IOException;
import java.util.Optional;

public class SignUpPage extends BaseController {
    @FXML
    private Button GoToWelcome;
    @FXML
    private Button SignInButton;
    @FXML
    private Button SignUpButton;
    @FXML
    private TextField username;
    @FXML
    private TextField lastName;
    @FXML
    private TextField firstName;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    private ServiceAuthentication serviceAuthentication;

    public SignUpPage() {
        try {
            this.serviceAuthentication = ApplicationContext.getInstance().getServiceAuthentication();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Service could not be initialized");
        }
    }

    @FXML
    public void initialize() {
        SignUpButton.setOnAction(event -> registerUser());
    }

    @FXML
    public void goToWelcome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
            fxmlLoader.setControllerFactory(controller -> new Welcome());
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) GoToWelcome.getScene().getWindow();
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Welcome failed!");
        }
    }

    @FXML
    public void goToLogIn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/LogInPage.fxml"));
            fxmlLoader.setControllerFactory(controller -> new LoginPage());
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) SignInButton.getScene().getWindow();
            stage.resizableProperty().setValue(false);
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    @FXML
    public void registerUser() {
        String username = this.username.getText();
        String password = this.password.getText();
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String email = this.email.getText();
        try {
            ValidationFields(username, password, firstName, lastName, email);
            Stage homeStage = new Stage();
            Optional<User> userRegister = serviceAuthentication.register(username, lastName, firstName, email, password, homeStage);
            if (userRegister.isPresent()) {
                homeStage.setTitle("Home" + " - " + username);
                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful register!", "User registered successfully");

                openHomeWindow(homeStage);

                this.password.clear();
                this.username.clear();
                this.email.clear();
                this.firstName.clear();
                this.lastName.clear();

            }
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    public static void ValidationFields(String username, String password, String firstName, String lastName, String email) {
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            throw new ValidationException("Please fill all fields");
        }
        if (password.length() < 6) {
            throw new ValidationException("Password should have at least 6 characters");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Please enter a valid email");
        }
    }
}
