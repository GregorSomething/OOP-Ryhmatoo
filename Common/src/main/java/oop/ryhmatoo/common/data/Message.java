package oop.ryhmatoo.common.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Nime esimesed viis sümboli on värvi koord talle omane
public record Message(String sender, String channel, String content, long timestamp, Type messageType) implements Comparable<Message> {

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
