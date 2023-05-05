package oop.ryhmatoo.common.data;


import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Nime esimesed viis sümboli on värvi koord talle omane
public record Message(String sender, String senderColor, String channel, String content, long timestamp, Type type) implements Comparable<Message> {

    @SneakyThrows
    public static Message from(ResultSet rs) {
        return new Message(rs.getString("sender"),
                rs.getString("s_color"),
                rs.getString("channel"),
                rs.getString("content"),
                rs.getLong("timestamp"),
                Type.valueOf(rs.getString("type")));
    }

    @Override
    public int compareTo(Message o) {
        return Long.compare(this.timestamp, o.timestamp);
    }

    public enum Type {
        MESSAGE,
        IMAGE,
        FILE;
    }
}
