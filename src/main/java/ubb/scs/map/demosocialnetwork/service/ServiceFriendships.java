package ubb.scs.map.demosocialnetwork.service;

import javafx.stage.Stage;
import ubb.scs.map.demosocialnetwork.context.ApplicationContext;
import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.repository.Repository;
import ubb.scs.map.demosocialnetwork.utils.events.Event;
import ubb.scs.map.demosocialnetwork.utils.events.EventType;
import ubb.scs.map.demosocialnetwork.utils.observer.Observable;
import ubb.scs.map.demosocialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceFriendships implements Observable<Friendship> {
    private final List<Observer<Friendship>> observers = new CopyOnWriteArrayList<>();
    private final Repository<Long, Friendship> friendshipRepository;
    private final ApplicationContext applicationContext;

    public ServiceFriendships(Repository<Long, Friendship> friendshipRepository, ApplicationContext applicationContext) {
        this.friendshipRepository = friendshipRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public void addObserver(Observer<Friendship> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<Friendship> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event<Friendship> event) {
        observers.forEach(observer -> observer.update(event));
    }

    public boolean verifyFriendshipWithUsersIds(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            return false;
        }
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship friendship : friendships) {
            boolean directFriendship = friendship.getIdUser1().equals(userId1) &&
                    friendship.getIdUser2().equals(userId2);
            boolean reverseFriendship = friendship.getIdUser1().equals(userId2) &&
                    friendship.getIdUser2().equals(userId1);

            if (directFriendship || reverseFriendship) {
                return true;
            }
        }
        return false;
    }

    public Optional<Friendship> sendFriendRequest(Long receiverId, Stage stage) {
        User user = applicationContext.getCurrentUser(stage);
        if (user == null) {
            return Optional.empty();
        }
        Long senderId = user.getId();

        if (verifyFriendshipWithUsersIds(senderId, receiverId)) {
            return Optional.empty();
        }

        Friendship newFriendship = new Friendship(senderId, receiverId, FriendshipsRequests.PENDING.getValue(), LocalDateTime.now());
        Optional<Friendship> friendship = friendshipRepository.save(newFriendship);

        friendship.ifPresent(friendship1 -> notifyObservers(new Event<>(EventType.SEND_FRIEND_REQUEST, friendship1)));

        return friendship;
    }

    public Optional<Friendship> deleteFriendship(Long Id) {
        Optional<Friendship> friendship = friendshipRepository.findOne(Id);

        if (friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipsRequests.ACCEPTED.getValue())) {
            return Optional.empty();
        }

        Optional<Friendship> friendshipDeleted = friendshipRepository.delete(Id);
        friendshipDeleted.ifPresent(value -> notifyObservers(new Event<>(EventType.DELETE_FRIENDSHIP, value)));

        return friendshipDeleted;
    }

    public boolean isOnPendingAndExists(Long friendId, Long currentUserId, String status) {
        return friendId.equals(currentUserId) && status.equals(FriendshipsRequests.PENDING.getValue());
    }

    public Optional<Friendship> acceptFriendRequest(Long Id, Stage stage) {
        Optional<Friendship> acceptedFriendship = friendshipRepository.findOne(Id);

        if (acceptedFriendship.isEmpty() || !isOnPendingAndExists(acceptedFriendship.get().getIdUser2(), applicationContext.getCurrentUser(stage).getId(),
                acceptedFriendship.get().getStatus())) {
            return Optional.empty();
        }

        acceptedFriendship.get().setStatus(FriendshipsRequests.ACCEPTED.getValue());
        acceptedFriendship.get().setDate(LocalDateTime.now());

        Optional<Friendship> updatedFriendship = friendshipRepository.update(acceptedFriendship.get());

        if (updatedFriendship.isEmpty()) {
            return Optional.empty();
        }

        notifyObservers(new Event<>(EventType.STATUS_FRIENDSHIP_ACCEPTED, updatedFriendship.get()));
        return updatedFriendship;
    }


    public Optional<Friendship> denyFriendRequest(Long Id) {
        Optional<Friendship> deniedFriendship = friendshipRepository.findOne(Id);
        if (deniedFriendship.isEmpty() || !deniedFriendship.get().getStatus().equals(FriendshipsRequests.PENDING.getValue())) {
            return Optional.empty();
        }

        deniedFriendship.get().setStatus(FriendshipsRequests.DENIED.getValue());
        deniedFriendship.get().setDate(LocalDateTime.now());

        Optional<Friendship> updatedFriendship = friendshipRepository.update(deniedFriendship.get());
        if (updatedFriendship.isEmpty())
            return Optional.empty();

        notifyObservers(new Event<>(EventType.STATUS_FRIENDSHIP_DENIED, updatedFriendship.get()));
        return updatedFriendship;

    }

    public boolean isCurrentUserEqualToUserTwo(Long Id, Stage stage) {
        return applicationContext.getCurrentUser(stage).getId().equals(Id);
    }

    public boolean isFriends(Friendship friendship, Stage stage) {
        return isCurrentUserEqualToUserTwo(friendship.getIdUser1(), stage) || isCurrentUserEqualToUserTwo((friendship.getIdUser2()), stage);
    }

    public List<Friendship> getCurrentUserFriendshipsAccepted(Stage stage) {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();

        friendships.forEach(friendship -> {
            if (isFriends(friendship, stage) && friendship.getStatus().equals(FriendshipsRequests.ACCEPTED.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

    public List<Friendship> getCurrentUserFriendshipsDenied(Stage stage) {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendship -> {
            if (isFriends(friendship, stage) && friendship.getStatus().equals(FriendshipsRequests.DENIED.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

    public List<Friendship> getCurrentUserFriendshipsPending(Stage stage) {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendship -> {
            if (isFriends(friendship, stage) && friendship.getStatus().equals(FriendshipsRequests.PENDING.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

}
