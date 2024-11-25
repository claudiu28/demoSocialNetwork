package ubb.scs.map.demosocialnetwork.service;

import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.repository.Repository;


import java.util.Optional;

public class ServiceAuthentication {
    Repository<Long, User> userRepository;

    public ServiceAuthentication(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String email, String password) {
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                CurrentUserSession.getInstance().setCurrentUser(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> register(String username, String lastName, String firstName, String email, String password) {
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email)) {
                return Optional.empty();
            }
        }
        User registerUser = new User(username, lastName, firstName, email, password);
        Optional<User> userAdded = userRepository.save(registerUser);
        if (userAdded.isPresent()) {
            CurrentUserSession.getInstance().setCurrentUser(userAdded.get());
            return userAdded;
        }
        return Optional.empty();
    }

    public void logout() {
        if (CurrentUserSession.getInstance().getCurrentUser() != null) {
            CurrentUserSession.getInstance().setCurrentUser(null);
        }
    }

}
