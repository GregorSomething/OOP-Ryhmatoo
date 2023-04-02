package oop.ryhmatoo.server.data;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.Server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageStorage {
    private final Database database;

    public MessageStorage() {
        database = Server.getInstance().getDatabase();
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try (ResultSet rs = this.database.query(Statments.GET_ALL_MESSAGES)) {
            if (!rs.isBeforeFirst()) return messages;
            while (rs.next()) {
                messages.add(Message.from(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public void saveMessage(Message message) {
        try {
            this.database.execute(Statments.INSERT_MESSAGE, message.sender(),
                    message.channel(), message.content(), message.timestamp());
        } catch (SQLException e) {
            // TODO, teemidagi.
            throw new RuntimeException(e);
        }
    }
}
