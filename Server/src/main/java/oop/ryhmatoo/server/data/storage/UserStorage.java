package oop.ryhmatoo.server.data.storage;

import lombok.NonNull;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.data.Statments;
import oop.ryhmatoo.server.data.records.ServerUser;

import java.sql.SQLException;
import java.util.List;

public class UserStorage {

    private final Database database;

    public UserStorage(Database database) {
        this.database = database;
    }

    public void saveNewUser(ServerUser user) throws SQLException {
        this.database.execute(Statments.INSERT_USER, user.name(), user.color(), user.salt(), user.hashedPassword());
    }

    public ServerUser getUser(String name) throws SQLException {
        return this.database.queryAndMap(Statments.GET_USER_BY_NAME, ServerUser::from, name)
                .stream().filter(u -> u.name().equals(name)) // Osa andmebaase vördlevad caseinsensitiv modes
                .reduce((u1, u2) -> u1).orElse(null);
    }




}
