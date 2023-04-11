package oop.ryhmatoo.server.data;

import oop.ryhmatoo.common.data.Channel;

import java.util.List;

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
}
