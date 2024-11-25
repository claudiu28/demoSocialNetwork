package ubb.scs.map.demosocialnetwork.repository.database;

import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.validator.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FriendshipsDB extends RepositoryDB<Long, Friendship> {

    public FriendshipsDB(Validator<Friendship> validator) throws Exception {
        super(validator, TableName.FRIENDSHIPS);
    }

    @Override
    protected Friendship transformToEntity(ResultSet entity) throws SQLException {
        Long IdFriendship = entity.getLong("IdFriendship");
        Long IdUser1 = entity.getLong("IdUser1");
        Long IdUser2 = entity.getLong("IdUser2");
        Timestamp timestamp = entity.getTimestamp("date");
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        String status = entity.getString("Status");
        Friendship friendship = new Friendship(IdUser1, IdUser2, status, localDateTime);
        friendship.setId(IdFriendship);
        return friendship;
    }

    @Override
    protected Map<String, Object> fromEntityValues(Friendship entity) {
        Map<String, Object> Friendships = new HashMap<>();
        Friendships.put("IdUser1", entity.getIdUser1());
        Friendships.put("IdUser2", entity.getIdUser2());
        Friendships.put("date", entity.getDate());
        Friendships.put("Status", entity.getStatus());
        return Friendships;
    }
}
