package ubb.scs.map.demosocialnetwork.factory;

import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.UserValidation;
import ubb.scs.map.demosocialnetwork.repository.database.RepositoryDB;
import ubb.scs.map.demosocialnetwork.repository.database.UsersDB;
import ubb.scs.map.demosocialnetwork.service.ServiceAuthentication;

import java.sql.SQLException;

public class ServiceAuthFactory {
    private volatile static ServiceAuthentication instance = null;

    public static ServiceAuthentication getInstance() {
        if (instance == null) {
            synchronized (ServiceAuthFactory.class) {
                if (instance == null) {
                    try {
                        RepositoryDB<Long, User> userRepository = new UsersDB(new UserValidation());
                        instance = new ServiceAuthentication(userRepository);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }
}
