package ubb.scs.map.demosocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.ValidationException;
import ubb.scs.map.demosocialnetwork.factory.ServiceAuthFactory;
import ubb.scs.map.demosocialnetwork.service.CurrentUserSession;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;


import java.io.IOException;
import java.util.Optional;

public class SignUpPage {
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

    private final ServiceAuthentication serviceAuthentication;

    public SignUpPage() {
        this.serviceAuthentication = ServiceAuthFactory.getInstance();
    }

    @FXML
    public void goToWelcome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/Welcome.fxml"));
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
            Optional<User> userRegister = serviceAuthentication.register(username, lastName, firstName, email, password);
            if (userRegister.isPresent()) {
                CurrentUserSession.getInstance().setCurrentUser(userRegister.get());
                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful register!", "User registered successfully");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/UsersHome.fxml"));
                Parent root = loader.load();
                UsersHome usersHome = loader.getController();
                Stage stage = (Stage) SignUpButton.getScene().getWindow();
                stage.resizableProperty().setValue(false);
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest(event -> usersHome.onClose());
                stage.show();
            }
        } catch (ValidationException e) {
            ValidationFields(username, password, firstName, lastName, email);
        } catch (IOException e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    public static void ValidationFields(String username, String password, String firstName, String lastName, String email) {
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Please fill all the fields");
        }
        if (password.length() < 6) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Password must be at least 6 characters");
        }
        if (!email.contains("@")) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Invalid Field", "Something went wrong!", "Please enter a valid email");
        }
    }
}
