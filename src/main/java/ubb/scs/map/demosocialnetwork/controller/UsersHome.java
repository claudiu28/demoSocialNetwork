package ubb.scs.map.demosocialnetwork.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.factory.ServiceAuthFactory;
import ubb.scs.map.demosocialnetwork.factory.ServiceFriendshipFactory;
import ubb.scs.map.demosocialnetwork.factory.ServiceUserFactory;
import ubb.scs.map.demosocialnetwork.service.CurrentUserSession;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;
import ubb.scs.map.demosocialnetwork.service.ServiceFriendships;
import ubb.scs.map.demosocialnetwork.service.ServiceUsers;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.events.EventType;
import ubb.scs.map.demosocialnetwork.utils.extract.Extract;
import ubb.scs.map.demosocialnetwork.utils.format.Format;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.util.List;

import java.util.Objects;
import java.util.Optional;

public class UsersHome implements Observer<User> {
    private final ServiceUsers serviceUsers;
    private final ServiceFriendships serviceFriendships;
    private final ServiceAuthentication serviceAuthentication;

    @FXML
    private ListView<StringBuilder> friendsPending, friendsAccepted, friendsDenied;

    @FXML
    private TableView<User> allUsers;

    @FXML
    private TableColumn<User, String> usernameTable;

    @FXML
    private TableColumn<User, String> emailTable;

    @FXML
    private Button logoutButton, deleteAccountButton, modifyProfileButton;

    @FXML
    private Button addFriendButton, removeFriendButton, acceptRequest, deniedRequest;

    @FXML
    private TextField usernameCurentUser, usernameFriend,
            emailCurentUser, emailFriend,
            lastNameCurentUser, lastNameFriend,
            firstNameCurentUser, firstNameFriend;

    @FXML
    private Text textEntry;

    public UsersHome() {
        this.serviceUsers = ServiceUserFactory.getInstance();
        this.serviceFriendships = ServiceFriendshipFactory.getInstance();
        this.serviceAuthentication = ServiceAuthFactory.getInstance();

    }

    @FXML
    public void initialize() {
        serviceUsers.addObserver(this);
        if (curentUserInformation()) return;

        deleteAccountButton.setOnAction(event -> deleteAccount());

        usernameTable.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailTable.setCellValueFactory(new PropertyValueFactory<>("email"));

        usernameTable.setSortable(false);
        emailTable.setSortable(false);

        populateTableView();
        friendsInfo();

    }

    @Override
    public void update(Event<User> event) {
        if(event.getType() == EventType.DELETE_USER){
            Platform.runLater(this::logout);
        }
        if(event.getType() == EventType.UPDATE_USER){
            modifyProfileButton.setDisable(true); // call modify function -> profile
        }
    }


    private boolean curentUserInformation() {
        if (CurrentUserSession.getInstance().getCurrentUser() == null) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "User is null!");
            return true;
        }
        textEntry.setText("Welcome " + CurrentUserSession.getInstance().getCurrentUser().getUsername() + "!");
        usernameCurentUser.setText(CurrentUserSession.getInstance().getCurrentUser().getUsername());
        emailCurentUser.setText(CurrentUserSession.getInstance().getCurrentUser().getEmail());
        lastNameCurentUser.setText(CurrentUserSession.getInstance().getCurrentUser().getLastName());
        firstNameCurentUser.setText(CurrentUserSession.getInstance().getCurrentUser().getFirstName());
        return false;
    }

    @FXML
    private void logout() {
        try {
            serviceAuthentication.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ubb/scs/map/demosocialnetwork/LogInPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.resizableProperty().setValue(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }

    private void deleteAccount() {
        try {
            Optional<User> userDeleted = serviceUsers.deleteUser(CurrentUserSession.getInstance().getCurrentUser().getId());
            if (userDeleted.isPresent()) {
                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful delete!", "User deleted successfully");
                logout();
            }
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Something went wrong");
        }
    }


    private void populateTableView() {
        allUsers.getItems().clear();
        List<User> listUsers = serviceUsers.getAllUsers();
        listUsers.removeIf(user ->
                user.getUsername().equals(CurrentUserSession.getInstance().getCurrentUser().getUsername()) &&
                        user.getEmail().equals(CurrentUserSession.getInstance().getCurrentUser().getEmail())
        );
        allUsers.getItems().addAll(listUsers);
    }

    private void friendsInfo() {
        usernameFriend.textProperty().bind(
                allUsers.getSelectionModel().selectedItemProperty().map(User::getUsername)
        );
        emailFriend.textProperty().bind(
                allUsers.getSelectionModel().selectedItemProperty().map(User::getEmail)
        );
        lastNameFriend.textProperty().bind(
                allUsers.getSelectionModel().selectedItemProperty().map(User::getLastName)
        );
        firstNameFriend.textProperty().bind(
                allUsers.getSelectionModel().selectedItemProperty().map(User::getFirstName)
        );

    }

    @FXML
    private void sendFriendRequest() {
        Long receiverId = allUsers.getSelectionModel().getSelectedItem().getId();
        boolean isFriends = serviceFriendships.VerifyFriendshipWithUsersIds(CurrentUserSession.getInstance().getCurrentUser().getId(), receiverId);
        if (isFriends) {
            addFriendButton.setDisable(true);
        } else {
            Optional<Friendship> friendship = serviceFriendships.sendFriendRequest(receiverId);
            if (friendship.isPresent()) {
                AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Successful send friend request!", "Friend request sent successfully");
            }
        }
    }

    StringBuilder formatList(Friendship friendship) {
        String username = getUsernameOfFriend(friendship);
        StringBuilder string = new StringBuilder();
        string.append("Username: ").append(username).append(" -> Date: ").append(Format.convert(friendship.getDate())).append("\n");
        return string;
    }

    private String getUsernameOfFriend(Friendship friendship) {
        String username = "";
        if (!friendship.getIdUser2().equals(CurrentUserSession.getInstance().getCurrentUser().getId())) {
            Optional<User> findUser = serviceUsers.getUserById(friendship.getIdUser2());
            if (findUser.isPresent()) {
                username = findUser.get().getUsername();
            }
        } else {
            Optional<User> findUser = serviceUsers.getUserById(friendship.getIdUser1());
            if (findUser.isPresent()) {
                username = findUser.get().getUsername();
            }
        }
        return username;
    }

    public void allPendingList() {
        List<Friendship> friendshipsPending = serviceFriendships.getCurrentUserFriendshipsPending();
        friendsPending.getItems().clear();
        friendshipsPending.forEach(friendship ->
        {
            StringBuilder myInput = formatList(friendship);
            friendsPending.getItems().add(myInput);

        });
    }

    public void allAcceptedList() {
        List<Friendship> friendshipsAccepted = serviceFriendships.getCurrentUserFriendshipsAccepted();
        friendsAccepted.getItems().clear();
        friendshipsAccepted.forEach(friendship ->
        {
            StringBuilder myInput = formatList(friendship);
            friendsAccepted.getItems().add(myInput);

        });
    }


    public void allDeniedList() {
        List<Friendship> friendshipsDenied = serviceFriendships.getCurrentUserFriendshipsDenied();
        friendsDenied.getItems().clear();
        friendshipsDenied.forEach(friendship ->
        {
            StringBuilder myInput = formatList(friendship);
            friendsDenied.getItems().add(myInput);

        });
    }


    @FXML
    public void acceptFriendRequest() {
        StringBuilder selectedItem = friendsPending.getSelectionModel().getSelectedItem();
        String username = Extract.extractUsername(selectedItem.toString());
        var users = serviceUsers.getAllUsers();
        User requestUser = users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        List<Friendship> friendshipsPending = serviceFriendships.getCurrentUserFriendshipsPending();

        List<Friendship> f = friendshipsPending.stream()
                .filter(friendship ->
                        (friendship.getIdUser1().equals(CurrentUserSession.getInstance().getCurrentUser().getId()) &&
                                friendship.getIdUser2().equals(Objects.requireNonNull(requestUser).getId())) ||
                                (friendship.getIdUser1().equals(Objects.requireNonNull(requestUser).getId()) &&
                                        friendship.getIdUser2().equals(CurrentUserSession.getInstance().getCurrentUser().getId())))
                .toList();
        System.out.println(f.getFirst().getId());
        Optional<Friendship> friendship = serviceFriendships.acceptFriendRequest(f.getFirst().getId());
        if (friendship.isPresent()) {
            friendsPending.getItems().remove(selectedItem);
            allPendingList();
            allAcceptedList();
            allDeniedList();
        }

    }

    @FXML
    public void onClose(){
        serviceUsers.removeObserver(this);

    }

}
