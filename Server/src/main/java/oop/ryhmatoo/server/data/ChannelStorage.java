package oop.ryhmatoo.server.data;

import oop.ryhmatoo.common.data.Channel;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelStorage {

    private final Database database;

    public ChannelStorage(Database database) {
        this.database = database;
    }

    public List<Channel> getAllChannels() {
        return this.database.queryAndMap(Statments.GET_ALL_CHANNELS, Channel::from);
    }

    public List<Channel> getChannelsForUser(String user) {
        return this.database.queryAndMap(Statments.GET_ALL_CHANNELS_FOR_USER, Channel::from, user);
    }

    public void saveNewChannel(Channel channel) {
        String members = ";" + channel.members().stream().collect(Collectors.joining(";")) + ";";
        try {
            this.database.execute(Statments.INSERT_CHANNEL, channel.name(), channel.canWrite(), members, channel.type());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
