package ubb.scs.map.demosocialnetwork.service;

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
    List<Observer<Friendship>> observerFriendshipsList = new CopyOnWriteArrayList<>();
    Repository<Long, Friendship> friendshipRepository;

    public ServiceFriendships(Repository<Long, Friendship> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public void addObserver(Observer<Friendship> observer) {
        observerFriendshipsList.add(observer);
    }

    @Override
    public void removeObserver(Observer<Friendship> observer) {
        observerFriendshipsList.remove(observer);
    }

    @Override
    public void notifyObservers(Event<Friendship> event) {
        observerFriendshipsList.forEach(observer -> observer.update(event));
    }

    public boolean VerifyFriendshipWithUsersIds(Long UId1, Long UId2) {
        for (Friendship friendship : friendshipRepository.findAll()) {
            if (friendship.getIdUser1().equals(UId1) && friendship.getIdUser2().equals(UId2)) {
                return true;
            } else if (friendship.getIdUser1().equals(UId2) && friendship.getIdUser2().equals(UId1)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Friendship> sendFriendRequest(Long receiverId) {
        User user = CurrentUserSession.getInstance().getCurrentUser();
        Long senderId = user.getId();

        if (VerifyFriendshipWithUsersIds(senderId, receiverId)) {
            return Optional.empty();
        }

        Friendship newFriendship = new Friendship(senderId, receiverId, FriendshipsRequests.PENDING.getValue(), LocalDateTime.now());
        Optional<Friendship> friendship = friendshipRepository.save(newFriendship);
        if (friendship.isPresent()) {
            notifyObservers(new Event<>(EventType.SEND_FRIEND_REQUEST, friendship.get()));
            return friendship;
        }
        return Optional.empty();
    }

    public Optional<Friendship> deleteFriendship(Long Id) {
        Optional<Friendship> friendship = friendshipRepository.findOne(Id);
        if (friendship.isPresent() && friendship.get().getStatus().equals(FriendshipsRequests.ACCEPTED.getValue())) {
            Optional<Friendship> friendshipDeleted = friendshipRepository.delete(Id);
            friendship.ifPresent(value -> notifyObservers(new Event<>(EventType.DELETE_FRIENDSHIP, value)));
            return friendshipDeleted;
        }
        return Optional.empty();
    }

    public boolean acceptMethodCondition(Long CurrentFriendship, Long CurrentUserId, String status) {
        return CurrentFriendship.equals(CurrentUserId) && status.equals(FriendshipsRequests.PENDING.getValue());
    }

    public Optional<Friendship> acceptFriendRequest(Long Id) {
        Optional<Friendship> acceptedFriendship = friendshipRepository.findOne(Id);
        if (acceptedFriendship.isPresent()) {
            if (acceptMethodCondition(acceptedFriendship.get().getIdUser2(), CurrentUserSession.getInstance().getCurrentUser().getId(),
                    acceptedFriendship.get().getStatus())) {

                acceptedFriendship.get().setStatus(FriendshipsRequests.ACCEPTED.getValue());
                acceptedFriendship.get().setDate(LocalDateTime.now());

                Optional<Friendship> updatedFriendship = friendshipRepository.update(acceptedFriendship.get());

                notifyObservers(new Event<>(EventType.STATUS_FRIENDSHIP_ACCEPTED, acceptedFriendship.get()));

                if (updatedFriendship.isPresent())
                    return acceptedFriendship;
            }
        }
        return Optional.empty();
    }


    public Optional<Friendship> deniedFriendRequest(Long Id) {
        Optional<Friendship> deniedFriendship = friendshipRepository.findOne(Id);
        if (deniedFriendship.isPresent() && deniedFriendship.get().getStatus().equals(FriendshipsRequests.PENDING.getValue())) {

            deniedFriendship.get().setStatus(FriendshipsRequests.DENIED.getValue());
            deniedFriendship.get().setDate(LocalDateTime.now());

            Optional<Friendship> updatedFriendship = friendshipRepository.update(deniedFriendship.get());

            notifyObservers(new Event<>(EventType.STATUS_FRIENDSHIP_DENIED, deniedFriendship.get()));
            if (updatedFriendship.isPresent())
                return deniedFriendship;
        }
        return Optional.empty();
    }

    public boolean MethodFriends(Long Id) {
        return CurrentUserSession.getInstance().getCurrentUser().getId().equals(Id);
    }

    public boolean TypeMethodFriends(Friendship friendship) {
        return MethodFriends(friendship.getIdUser1()) || MethodFriends((friendship.getIdUser2()));
    }

    public List<Friendship> getCurrentUserFriendshipsAccepted() {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendship -> {
            if (TypeMethodFriends(friendship) && friendship.getStatus().equals(FriendshipsRequests.ACCEPTED.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

    public List<Friendship> getCurrentUserFriendshipsDenied() {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendship -> {
            if (TypeMethodFriends(friendship) && friendship.getStatus().equals(FriendshipsRequests.DENIED.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

    public List<Friendship> getCurrentUserFriendshipsPending() {
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendship -> {
            if (TypeMethodFriends(friendship) && friendship.getStatus().equals(FriendshipsRequests.PENDING.getValue())) {
                friendshipList.add(friendship);
            }
        });
        return friendshipList;
    }

}
