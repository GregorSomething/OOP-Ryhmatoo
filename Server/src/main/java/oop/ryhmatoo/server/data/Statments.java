package oop.ryhmatoo.server.data;

public enum Statments implements SQLStatement {
    GET_ALL_MESSAGES("SELECT * FROM messages;"),
    GET_MESSAGES_IN_CHANNEL_LIMIT("SELECT * FROM messages WHERE channel = ? LIMIT ?;"),
    INSERT_MESSAGE("""
            INSERT INTO messages
            (sender, channel, content, "timestamp", "type")
            VALUES(?, ?, ?, ?, ?);"""),

    GET_ALL_CHANNELS("SELECT * FROM channels;"),
    GET_ALL_CHANNELS_FOR_USER("SELECT * FROM channels WHERE members LIKE '%;' || ? || ';%';"),
    INSERT_CHANNEL("""
            INSERT INTO channels
            (name, canWrite, members, "type")
            VALUES(?, ?, ?, ?);""");

    private final String content;

    Statments(String content) {
        this.content = content;
    }

    @Override
    public String get() {
        return this.content;
    }
}
