package ubb.scs.map.demosocialnetwork.service;

import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.context.UserContext;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.repository.Repository;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.observer.Observable;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceAuthentication {
    private final Repository<Long, User> userRepository;
    private final ApplicationContext applicationContext;

    public ServiceAuthentication(Repository<Long, User> userRepository, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }

    public Optional<User> login(String username, String email, String password, Stage stage) {
        if (username == null || email == null || password == null ||
                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                UserContext userContext = new UserContext(user, stage);
                applicationContext.registerSession(stage, userContext);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> register(String username, String lastName, String firstName, String email, String password, Stage stage) {
        if (username == null || lastName == null || firstName == null ||
                email == null || password == null ||
                username.isEmpty() || lastName.isEmpty() || firstName.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email)) {
                return Optional.empty();
            }
        }
        User registerUser = new User(username, lastName, firstName, email, password);
        Optional<User> userAdded = userRepository.save(registerUser);
        userAdded.ifPresent(user -> {
            UserContext userContext = new UserContext(user, stage);
            applicationContext.registerSession(stage, userContext);
        });
        return userAdded;
    }

    public void logout(Stage stage) {
        User currentUser = applicationContext.getCurrentUser(stage);
        if (currentUser != null) {
            applicationContext.unregisterSession(stage);
        }
    }
}
