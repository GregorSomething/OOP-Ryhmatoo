package oop.ryhmatoo.common.data;

import java.util.List;

public record Channel(String name, boolean canWrite, List<String> members, Type channelType) {

    public enum Type {
        DIRECT_MESSAGE,
        CHANNEL;
    }
}
