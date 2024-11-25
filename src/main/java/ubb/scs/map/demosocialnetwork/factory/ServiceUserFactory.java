package ubb.scs.map.demosocialnetwork.factory;

import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.UserValidation;
import ubb.scs.map.demosocialnetwork.repository.database.RepositoryDB;
import ubb.scs.map.demosocialnetwork.repository.database.UsersDB;
import ubb.scs.map.demosocialnetwork.service.ServiceUsers;

public class ServiceUserFactory {
    private static volatile ServiceUsers instance = null;

    public static ServiceUsers getInstance() {
        if (instance == null) {
            synchronized (ServiceUsers.class) {
                if (instance == null) {
                    try {
                        RepositoryDB<Long, User> userRepository = new UsersDB(new UserValidation());
                        instance = new ServiceUsers(userRepository);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }
}
