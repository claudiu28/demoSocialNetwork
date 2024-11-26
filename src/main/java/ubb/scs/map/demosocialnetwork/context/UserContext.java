package ubb.scs.map.demosocialnetwork.context;

import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.domain.User;

public class UserContext {
    private User currentUser;
    private Stage stage;

    public UserContext() {
    }

    public UserContext(User user, Stage stage) {
        this.currentUser = user;
        this.stage = stage;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isForUser(Long userId) {
        return currentUser != null && currentUser.getId().equals(userId);
    }


}
