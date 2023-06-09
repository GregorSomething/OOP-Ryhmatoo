package oop.ryhmatoo.server.data.storage;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.data.Statments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageStorage {
    private final Database database;

    public MessageStorage(Database database) {
        this.database = database;
    }

    public List<Message> getAllMessages() throws SQLException {
        return this.database.queryAndMap(Statments.GET_ALL_MESSAGES, Message::from);
    }

    public List<Message> getMessagesInChannelLimit(String channel, int limit) throws SQLException {
        return this.database.queryAndMap(Statments.GET_MESSAGES_IN_CHANNEL_LIMIT, Message::from, channel, limit);
    }

    public void saveMessage(Message message) throws SQLException {
        this.database.execute(Statments.INSERT_MESSAGE, message.sender(),
                message.channel(), message.content(), message.timestamp(), message.type());
    }
}
