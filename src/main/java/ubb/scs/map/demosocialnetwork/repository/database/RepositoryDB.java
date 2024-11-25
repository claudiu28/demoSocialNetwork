package ubb.scs.map.demosocialnetwork.repository.database;

import ubb.scs.map.demosocialnetwork.config.Config;
import ubb.scs.map.demosocialnetwork.domain.Entity;
import ubb.scs.map.demosocialnetwork.domain.validator.ValidationException;
import ubb.scs.map.demosocialnetwork.domain.validator.Validator;
import ubb.scs.map.demosocialnetwork.repository.Repository;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class RepositoryDB<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validator<E> validator;
    protected final TableName tableName;


    private final Connection connection;

    public RepositoryDB(Validator<E> validator, TableName tableName) throws SQLException {
        Config config = Config.getInstance();
        this.connection = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseUser(), config.getDatabasePassword());

        this.validator = validator;
        this.tableName = tableName;
    }


    protected abstract E transformToEntity(ResultSet entity) throws SQLException;

    protected abstract Map<String, Object> fromEntityValues(E entity);

    private StringBuilder sqlEntityInsertCommand(Map<String, Object> values) {
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName.getName() + "(");
        values.forEach((k, v) -> insertSql.append(k).append(", "));
        insertSql.delete(insertSql.length() - 2, insertSql.length());
        insertSql.append(") VALUES (");
        values.forEach((k, v) -> insertSql.append("?, "));
        insertSql.delete(insertSql.length() - 2, insertSql.length());
        insertSql.append(")");
        return insertSql;
    }

    private StringBuilder sqlEntityDeleteCommand() {
        StringBuilder deleteSql = new StringBuilder("DELETE FROM " + tableName.getName() + " WHERE ");
        if (tableName == TableName.USERS) {
            deleteSql.append("IdUser = ?");
        } else if (tableName == TableName.FRIENDSHIPS) {
            deleteSql.append("IdFriendship = ?");
        }
        return deleteSql;
    }

    private StringBuilder sqlEntityUpdateCommand(Map<String, Object> values) {
        StringBuilder updateSql = new StringBuilder("UPDATE " + tableName.getName() + " SET ");
        values.forEach((k, v) -> updateSql.append(k).append(" = ?, "));
        updateSql.delete(updateSql.length() - 2, updateSql.length());
        updateSql.append(" WHERE ");
        if (tableName == TableName.USERS) {
            updateSql.append("IdUser = ?");
        } else if (tableName == TableName.FRIENDSHIPS) {
            updateSql.append("IdFriendship = ?");
        }
        return updateSql;
    }

    private StringBuilder sqlEntityFindCommand() {
        StringBuilder findSql = new StringBuilder("SELECT * FROM " + tableName.getName() + " WHERE ");
        if (tableName == TableName.USERS) {
            findSql.append("IdUser = ?");
        } else if (tableName == TableName.FRIENDSHIPS) {
            findSql.append("IdFriendship = ?");
        }
        return findSql;
    }

    private StringBuilder sqlEntityFindAllCommand() {
        return new StringBuilder("SELECT * FROM " + tableName.getName());
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null) {
            throw new ValidationException("The id must not be null!");
        }
        StringBuilder findSql = sqlEntityFindCommand();
        try (PreparedStatement statement = connection.prepareStatement(findSql.toString())) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(transformToEntity(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
    }

    @Override
    public Iterable<E> findAll() {
        StringBuilder findSql = sqlEntityFindAllCommand();
        List<E> entities = new java.util.ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(findSql.toString())) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                entities.add(transformToEntity(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
    }

    @Override
    public Optional<E> save(E entity) {
        validator.validate(entity);
        Map<String, Object> values = fromEntityValues(entity);

        try (PreparedStatement statement = connection.prepareStatement(sqlEntityInsertCommand(values).toString(), Statement.RETURN_GENERATED_KEYS)) {
            int indexValues = 1;
            for (Object value : values.values()) {
                statement.setObject(indexValues++, value);
            }
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ValidationException("The entity was not saved!");
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long Id = generatedKeys.getLong(1);
                entity.setId((ID) Id);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new ValidationException("The id must not be null!");
        }
        Optional<E> Entity = findOne(id);
        try (PreparedStatement statement = connection.prepareStatement(sqlEntityDeleteCommand().toString())) {
            statement.setObject(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ValidationException("The entity was not deleted!");
            }
            return Entity;
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
    }

    @Override
    public Optional<E> update(E entity) {
        validator.validate(entity);
        Map<String, Object> values = fromEntityValues(entity);
        Optional<E> entityToUpdate = findOne(entity.getId());
        try (PreparedStatement statement = connection.prepareStatement(sqlEntityUpdateCommand(values).toString())) {
            int indexValues = 1;
            for (Object value : values.values()) {
                statement.setObject(indexValues++, value);
            }
            statement.setObject(indexValues, entity.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ValidationException("The entity was not updated!");
            }
            return entityToUpdate;
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
    }

    public void connectionClose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new ValidationException(e);
        }
    }
}
