package ubb.scs.map.demosocialnetwork.context;

import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.FriendshipValidation;
import ubb.scs.map.demosocialnetwork.domain.validator.UserValidation;
import ubb.scs.map.demosocialnetwork.repository.Repository;
import ubb.scs.map.demosocialnetwork.repository.database.FriendshipsDB;
import ubb.scs.map.demosocialnetwork.repository.database.UsersDB;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;
import ubb.scs.map.demosocialnetwork.service.ServiceFriendships;
import ubb.scs.map.demosocialnetwork.service.ServiceUsers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private static ApplicationContext instance;

    private final ServiceUsers serviceUsers;
    private final ServiceFriendships serviceFriendships;
    private final ServiceAuthentication serviceAuthentication;

    private final Map<Stage, UserContext> activeUserSessions = new ConcurrentHashMap<>();


    private ApplicationContext() throws Exception {
        Repository<Long, User> userRepository = new UsersDB(new UserValidation());
        Repository<Long, Friendship> friendshipRepository = new FriendshipsDB(new FriendshipValidation());
        serviceUsers = new ServiceUsers(userRepository, this);
        serviceFriendships = new ServiceFriendships(friendshipRepository, this);
        serviceAuthentication = new ServiceAuthentication(userRepository, this);
    }

    public static synchronized ApplicationContext getInstance() throws Exception {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public void registerSession(Stage stage, UserContext userContext) {
        activeUserSessions.put(stage, userContext);
        stage.setOnCloseRequest(event -> unregisterSession(stage));
    }

    public void unregisterSession(Stage stage) {
        activeUserSessions.remove(stage);
    }

    public UserContext getSession(Stage stage) {
        return activeUserSessions.get(stage);
    }


    public ServiceUsers getServiceUsers() {
        return serviceUsers;
    }

    public ServiceFriendships getServiceFriendships() {
        return serviceFriendships;
    }

    public ServiceAuthentication getServiceAuthentication() {
        return serviceAuthentication;
    }

    public User getCurrentUser(Stage stage) {
        UserContext userContext = activeUserSessions.get(stage);
        if (userContext != null) {
            return userContext.getCurrentUser();
        }
        return null;
    }

    public Set<Stage> getAllStagesUnique() {
        return activeUserSessions.keySet();
    }

    public boolean isUserLoggedIn(Long userId) {
        return activeUserSessions.values().stream()
                .map(UserContext::getCurrentUser)
                .anyMatch(user -> user != null && user.getId().equals(userId));
    }

    public Set<Stage> getStagesForUser(Long userId) {
        return activeUserSessions.entrySet().stream()
                .filter(entry -> {
                    User user = entry.getValue().getCurrentUser();
                    return user != null && user.getId().equals(userId);
                })
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

}
