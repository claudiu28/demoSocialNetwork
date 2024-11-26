package ubb.scs.map.demosocialnetwork.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.controller.alert.AlertShow;
import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;
import ubb.scs.map.demosocialnetwork.service.ServiceFriendships;
import ubb.scs.map.demosocialnetwork.service.ServiceUsers;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.events.EventType;
import ubb.scs.map.demosocialnetwork.utils.extract.Extract;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.Optional;

public class UsersHome {
    private final Stage currentStage;
    private final ServiceUsers serviceUsers;
    private final ServiceFriendships serviceFriendships;
    private final ServiceAuthentication serviceAuthentication;

    @FXML
    private ListView<StringBuilder> friendsPending, friendsAccepted, friendsDenied;

    @FXML
    private TableView<User> allUsers;
    @FXML
    private TableView<User> acceptedFriends;

    @FXML
    private TableColumn<User, String> usernameTable, friendsUsernameTable;

    @FXML
    private TableColumn<User, String> emailTable, emailUsernameTable;

    @FXML
    private Button logoutButton, deleteAccountButton, modifyProfileButton, addFriendButton, removeFriendButton, acceptRequestButton, deniedRequestButton;

    @FXML
    private TextField usernameCurentUser, usernameFriend, emailCurentUser, emailFriend, lastNameCurentUser, lastNameFriend, firstNameCurentUser, firstNameFriend;

    @FXML
    private Text textEntry;

    private final UserObserver userObserver;
    private final FriendshipObserver friendshipObserver;

    ApplicationContext context;

    public UsersHome(Stage stage) {
        try {
            this.currentStage = stage;
            context = ApplicationContext.getInstance();
            this.serviceUsers = context.getServiceUsers();
            this.serviceFriendships = context.getServiceFriendships();
            this.serviceAuthentication = context.getServiceAuthentication();

            this.userObserver = new UserObserver();
            this.friendshipObserver = new FriendshipObserver();

            this.serviceFriendships.addObserver(friendshipObserver);
            this.serviceUsers.addObserver(userObserver);

        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Service initialization failed!");
            throw new RuntimeException("Service initialization failed!");
        }
    }

    private class UserObserver implements Observer<User> {
        @Override
        public void update(Event<User> event) {
            User currentUser = context.getCurrentUser(currentStage);
            if (currentUser == null) return;

            Platform.runLater(() -> {
                if (event.getType() == EventType.USER_DELETED) {
                    handleUserDeleted(event.getData());
                }
            });
        }
    }

    private void handleUserDeleted(User data) {
        User currentUser = context.getCurrentUser(currentStage);
        if (currentUser == null || currentUser.getId().equals(data.getId())) {
            cleanup();
            currentStage.close();
        } else {
            loadAllData();
        }
    }

    private class FriendshipObserver implements Observer<Friendship> {
        @Override
        public void update(Event<Friendship> event) {
            User currentUser = context.getCurrentUser(currentStage);
            if (currentUser == null) return;

            Friendship friendship = event.getData();
            if (friendship.getIdUser1().equals(currentUser.getId()) || friendship.getIdUser2().equals(currentUser.getId())) {

                Platform.runLater(() -> {
                    switch (event.getType()) {
                        case SEND_FRIEND_REQUEST -> handleFriendsPending();
                        case STATUS_FRIENDSHIP_ACCEPTED -> handleFriendsAccepted();
                        case STATUS_FRIENDSHIP_DENIED -> handleFriendsDenied();
                        case DELETE_FRIENDSHIP -> handleFriendsDelete();
                    }
                });
            }
        }
    }

    private void handleFriendsPending() {
        refreshPendingList();
    }

    private void handleFriendsAccepted() {
        refreshAcceptedList();
        refreshPendingList();
        refreshAcceptedFriendsTable();
    }

    private void handleFriendsDenied() {
        refreshDeniedList();
        refreshPendingList();
    }

    private void handleFriendsDelete() {
        refreshPendingList();
        refreshAcceptedFriendsTable();
        refreshDeniedList();
        refreshAcceptedList();
    }

    @FXML
    public void initialize() {
        try {
            setupColumnTable();
            setupButtons();
            setupCurrentUserInformation();
            loadAllData();
            currentStage.setOnCloseRequest(event -> cleanup());
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong!", "Home page initialization failed!");
        }
    }

    private void clearAllLists() {
        friendsPending.getItems().clear();
        friendsAccepted.getItems().clear();
        friendsDenied.getItems().clear();
    }

    private void setupColumnTable() {
        usernameTable.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailTable.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendsUsernameTable.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailUsernameTable.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void setupButtons() throws Exception {
        logoutButton.setOnAction(event -> logout());
        deleteAccountButton.setOnAction(event -> deleteAccount());
        addFriendButton.setOnAction(event -> sendRequest());
        acceptRequestButton.setOnAction(event -> acceptRequest());
        deniedRequestButton.setOnAction(event -> deniedRequest());
        removeFriendButton.setOnAction(event -> removeFriend());
    }

    private void setupCurrentUserInformation() throws Exception {
        User currentUser = ApplicationContext.getInstance().getCurrentUser(currentStage);
        if (currentUser == null) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "No user logged in", "Session error");
            throw new Exception("User not logged in");
        }
        usernameCurentUser.setText(currentUser.getUsername());
        emailCurentUser.setText(currentUser.getEmail());
        lastNameCurentUser.setText(currentUser.getLastName());
        firstNameCurentUser.setText(currentUser.getFirstName());
        textEntry.setText("Welcome " + currentUser.getUsername());

        allUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> selectedFriend(newValue));
        });
    }

    private void selectedFriend(User selectedUser) {
        if (selectedUser == null) {
            usernameFriend.clear();
            emailFriend.clear();
            lastNameFriend.clear();
            firstNameFriend.clear();
            return;
        }
        usernameFriend.setText(selectedUser.getUsername());
        emailFriend.setText(selectedUser.getEmail());
        lastNameFriend.setText(selectedUser.getLastName());
        firstNameFriend.setText(selectedUser.getFirstName());
    }

    private boolean isFriendshipBetweenUsers(Friendship friendship, User otherUser) {
        User currentUser = context.getCurrentUser(currentStage);
        return (friendship.getIdUser1().equals(currentUser.getId()) &&
                friendship.getIdUser2().equals(otherUser.getId())) ||
                (friendship.getIdUser1().equals(otherUser.getId()) &&
                        friendship.getIdUser2().equals(currentUser.getId()));
    }

    public void loadAllData() {
        refreshAllUsers();
        refreshAcceptedFriendsTable();
        refreshPendingList();
        refreshAcceptedList();
        refreshDeniedList();
    }

    private void refreshAcceptedFriendsTable() {
        User currentUser = context.getCurrentUser(currentStage);
        if (currentUser == null) return;

        acceptedFriends.getItems().clear();
        List<User> acceptedFriendUsers = new ArrayList<>();

        serviceFriendships.getCurrentUserFriendshipsAccepted(currentStage).forEach(friendship -> {
            Long friendId = friendship.getIdUser1().equals(currentUser.getId()) ? friendship.getIdUser2() : friendship.getIdUser1();
            serviceUsers.getUserById(friendId).ifPresent(acceptedFriendUsers::add);
        });

        acceptedFriends.getItems().addAll(acceptedFriendUsers);
    }

    private void refreshAllUsers() {
        User currentUser = context.getCurrentUser(currentStage);
        if (currentUser == null) return;
        allUsers.getItems().clear();
        List<User> users = serviceUsers.getAllUsers();
        users.removeIf(user -> user.getId().equals(currentUser.getId()));
        allUsers.getItems().addAll(users);
    }

    private void refreshPendingList() {
        friendsPending.getItems().clear();
        serviceFriendships.getCurrentUserFriendshipsPending(currentStage).forEach(friendship -> {
            friendsPending.getItems().addAll(formatFriendshipInfo(friendship));
        });
    }

    private void refreshAcceptedList() {
        friendsAccepted.getItems().clear();
        serviceFriendships.getCurrentUserFriendshipsAccepted(currentStage).forEach(friendship -> {
            friendsAccepted.getItems().addAll(formatFriendshipInfo(friendship));
        });
    }

    private void refreshDeniedList() {
        friendsDenied.getItems().clear();
        serviceFriendships.getCurrentUserFriendshipsDenied(currentStage).forEach(friendship -> {
            friendsDenied.getItems().addAll(formatFriendshipInfo(friendship));
        });
    }

    private StringBuilder formatFriendshipInfo(Friendship friendship) {
        User currentUser = context.getCurrentUser(currentStage);
        Long friendId = friendship.getIdUser1().equals(currentUser.getId()) ? friendship.getIdUser2() : friendship.getIdUser1();

        User friend = serviceUsers.getUserById(friendId).orElse(null);
        if (friend == null) return new StringBuilder("Unknown user");

        return new StringBuilder().append("Username: ").append(friend.getUsername()).append(" -> Date: ").append(friendship.getDate());
    }

    @FXML
    private void logout() {
        try {
            serviceAuthentication.logout(currentStage);
            cleanup();
            currentStage.close();
        } catch (Exception e) {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Logout failed", e.getMessage());
        }
    }

    private void deleteAccount() {
        User currentUser = context.getCurrentUser(currentStage);
        if (currentUser == null) return;

        Optional<User> deletedUser = serviceUsers.deleteUser(currentUser.getId(), currentStage);
        if (deletedUser.isPresent()) {
            AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Account deleted", "Your account has been deleted");
            cleanup();
            currentStage.close();
        }
    }

    private void sendRequest() {
        var selectedUser = allUsers.getSelectionModel().getSelectedItem();
        if (allUsers.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        Optional<Friendship> friendship = serviceFriendships.sendFriendRequest(selectedUser.getId(), this.currentStage);
        if (friendship.isPresent()) {
            AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Friend request sent to" + selectedUser.getUsername() + "!", "Friend request sent");
        } else {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Friend request failed");
        }
    }

    private void acceptRequest() {
        Optional<Friendship> friendshipFind = findFriendshipInPending();
        if (friendshipFind.isEmpty()) return;

        Friendship friendship = friendshipFind.get();
        Optional<Friendship> friendshipAccepted = serviceFriendships.acceptFriendRequest(friendship.getId(), currentStage);
        if (friendshipAccepted.isPresent()) {
            AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Friend request accepted!", "Friend request accepted");
        } else {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Friend request failed");
        }
    }

    private Optional<User> findRequestUser(ListView<StringBuilder> list) {
        StringBuilder selectedUser = list.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            return Optional.empty();
        }

        String username = Extract.extractUsername(selectedUser.toString());
        return serviceUsers.getAllUsers().stream().filter(user -> Objects.equals(user.getUsername(), username)).findFirst();

    }

    private Optional<Friendship> findFriendshipInPending() {

        Optional<User> requestedUser = findRequestUser(friendsPending);
        if (requestedUser.isEmpty()) {
            return Optional.empty();
        }

        List<Friendship> pendingList = serviceFriendships.getCurrentUserFriendshipsPending(currentStage);

        return pendingList.stream().
                filter(friendship -> isFriendshipBetweenUsers(friendship, requestedUser.get())).findFirst();
    }

    private void deniedRequest() {
        Optional<Friendship> friendship = findFriendshipInPending();
        if (friendship.isEmpty()) return;

        Friendship friendshipDenied = friendship.get();
        Optional<Friendship> friendshipDeny = serviceFriendships.denyFriendRequest(friendshipDenied.getId());
        if (friendshipDeny.isPresent()) {
            AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Friend request denied!", "Friend request denied");
        } else {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Friend request failed");
        }

    }

    private Optional<Friendship> findFriendshipInAcceptedList() {

        Optional<User> requestedUser = findRequestUser(friendsAccepted);
        if (requestedUser.isEmpty()) {
            return Optional.empty();
        }
        List<Friendship> pendingList = serviceFriendships.getCurrentUserFriendshipsAccepted(currentStage);

        return pendingList.stream().
                filter(friendship -> isFriendshipBetweenUsers(friendship, requestedUser.get())).findFirst();
    }

    private void removeFriend() {
        Optional<Friendship> friendship = findFriendshipInAcceptedList();
        if (friendship.isEmpty()) return;

        Optional<Friendship> friendshipDeleted = serviceFriendships.deleteFriendship(friendship.get().getId());
        if (friendshipDeleted.isPresent()) {
            AlertShow.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Friendship deleted!", "Friendship deleted");
        } else {
            AlertShow.showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong", "Friendship failed");
        }
    }

    private void cleanup() {
        serviceUsers.removeObserver(userObserver);
        serviceFriendships.removeObserver(friendshipObserver);
    }
}
