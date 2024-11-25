package ubb.scs.map.demosocialnetwork.factory;

import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.validator.FriendshipValidation;
import ubb.scs.map.demosocialnetwork.repository.database.FriendshipsDB;
import ubb.scs.map.demosocialnetwork.repository.database.RepositoryDB;
import ubb.scs.map.demosocialnetwork.service.ServiceFriendships;

public class ServiceFriendshipFactory {
    private static volatile ServiceFriendships instance = null;

    public static ServiceFriendships getInstance() {
        if (instance == null) {
            synchronized (ServiceFriendshipFactory.class) {
                if (instance == null) {
                    try {
                        RepositoryDB<Long, Friendship> friendshipRepository = new FriendshipsDB(new FriendshipValidation());
                        instance = new ServiceFriendships(friendshipRepository);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }

}
