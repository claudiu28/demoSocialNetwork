package ubb.scs.map.demosocialnetwork.repository.database;

import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UsersDB extends RepositoryDB<Long, User> {
    public UsersDB(Validator<User> validator) throws SQLException {
        super(validator, TableName.USERS);
    }

    @Override
    protected User transformToEntity(ResultSet entity) throws SQLException {
        Long IdUser = entity.getLong("IdUser");
        String Username = entity.getString("Username");
        String LastName = entity.getString("LastName");
        String FirstName = entity.getString("FirstName");
        String Email = entity.getString("Email");
        String Password = entity.getString("Password");
        User user = new User(Username, LastName, FirstName, Email, Password);
        user.setId(IdUser);
        return user;
    }

    @Override
    protected Map<String, Object> fromEntityValues(User entity) {
        Map<String, Object> Users = new HashMap<>();
        Users.put("Username", entity.getUsername());
        Users.put("LastName", entity.getLastName());
        Users.put("FirstName", entity.getFirstName());
        Users.put("Email", entity.getEmail());
        Users.put("Password", entity.getPassword());
        return Users;
    }
}
