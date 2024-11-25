package ubb.scs.map.demosocialnetwork.service;

import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.repository.Repository;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.events.EventType;
import ubb.scs.map.demosocialnetwork.utils.observer.Observable;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServiceUsers implements Observable<User> {
    private final List<Observer<User>> observerList =  new CopyOnWriteArrayList<>();
    Repository<Long, User> userRepository;

    public ServiceUsers(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addObserver(Observer<User> observer) {
        if (!observerList.contains(observer)) {observerList.add(observer);}
    }

    @Override
    public void removeObserver(Observer<User> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(Event<User> event) {
        observerList.forEach(observer -> observer.update(event));
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

    public Optional<User> deleteUser(Long id) {
        Optional<User> deletedUser = userRepository.delete(id);
        deletedUser.ifPresent(user -> notifyObservers(new Event<>(EventType.DELETE_USER, user)));
        return deletedUser;
    }

    public Optional<User> updateUser(User user) {
        user.setId(CurrentUserSession.getInstance().getCurrentUser().getId());
        Optional<User> updatedUser = userRepository.update(user);
        updatedUser.ifPresent(u -> notifyObservers(new Event<>(EventType.UPDATE_USER, u)));
        return updatedUser;
    }

}
