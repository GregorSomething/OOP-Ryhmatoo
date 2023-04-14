package oop.ryhmatoo.server.data.storage;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.data.Statments;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelStorage {

    private final Database database;

    public ChannelStorage(Database database) {
        this.database = database;
    }

    public List<Channel> getAllChannels() throws SQLException {
        return this.database.queryAndMap(Statments.GET_ALL_CHANNELS, Channel::from);
    }

    public List<Channel> getChannelsForUser(String user) throws SQLException {
        return this.database.queryAndMap(Statments.GET_ALL_CHANNELS_FOR_USER, Channel::from, user);
    }

    public void saveNewChannel(Channel channel) throws SQLException {
        String members = ";" + channel.members().stream().collect(Collectors.joining(";")) + ";";
        this.database.execute(Statments.INSERT_CHANNEL, channel.name(), channel.canWrite(), members, channel.type());
    }

    public Channel getChannelByName(String name) throws SQLException {
        return this.database.queryAndMap(Statments.GET_CHANNEL_BY_NAME, Channel::from, name)
                .stream().filter(c -> c.name().equals(name)) // Osa andmebaase vördlevad caseinsensitiv modes
                .reduce((c1, c2) -> c1).orElse(null);
    }
}
