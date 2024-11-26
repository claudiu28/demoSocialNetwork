package ubb.scs.map.demosocialnetwork.service;

import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.context.UserContext;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.repository.Repository;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.events.EventType;
import ubb.scs.map.demosocialnetwork.utils.observer.Observable;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServiceUsers implements Observable<User> {
    private final List<Observer<User>> observerList = new CopyOnWriteArrayList<>();
    private final Repository<Long, User> userRepository;
    private final ApplicationContext applicationContext;

    public ServiceUsers(Repository<Long, User> userRepository, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public void addObserver(Observer<User> observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer<User> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(Event<User> event) {
        observerList.forEach(observer -> {
            observer.update(event);
        });
    }

    public List<User> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        List<User> usersList = new ArrayList<>();

        users.forEach(usersList::add);

        return usersList;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findOne(id);
    }

    public Optional<User> deleteUser(Long id, Stage stage) {
        Optional<User> findUser = userRepository.findOne(id);
        if (findUser.isEmpty()) {
            return Optional.empty();
        }
        Optional<User> deletedUser = userRepository.delete(id);
        deletedUser.ifPresent(user -> {
            notifyObservers(new Event<>(EventType.USER_DELETED, user));
            Set<Stage> deletedUserStages = applicationContext.getStagesForUser(user.getId());
            deletedUserStages.forEach(applicationContext::unregisterSession);
        });
        return deletedUser;
    }

    public Optional<User> updateUser(User user, Stage stage) {
        User currentUser = applicationContext.getCurrentUser(stage);
        if (currentUser == null) {
            return Optional.empty();
        }
        user.setId(currentUser.getId());
        Optional<User> updatedUser = userRepository.update(user);
        updatedUser.ifPresent(u -> {
            UserContext userContext = applicationContext.getSession(stage);
            if (userContext != null) {
                userContext.setCurrentUser(u);
            }
            notifyObservers(new Event<>(EventType.USER_UPDATED, u));
        });
        return updatedUser;
    }

}
