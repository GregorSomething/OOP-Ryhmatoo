package oop.ryhmatoo.server.data;

public enum Statments implements SQLStatement {
    GET_ALL_MESSAGES("SELECT m.*, u.color AS s_color FROM messages m LEFT JOIN users u ON u.name = m.sender;"),
    GET_MESSAGES_IN_CHANNEL_LIMIT("SELECT m.*, u.color AS s_color FROM messages m LEFT JOIN users u ON u.name = m.sender WHERE channel = ? LIMIT ?;"),
    INSERT_MESSAGE("""
            INSERT INTO messages
            (sender, channel, content, "timestamp", "type")
            VALUES(?, ?, ?, ?, ?);"""),

    GET_ALL_CHANNELS("SELECT * FROM channels;"),
    GET_ALL_CHANNELS_FOR_USER("SELECT * FROM channels WHERE members LIKE '%;' || ? || ';%';"),
    GET_CHANNEL_BY_NAME("SELECT * FROM channels WHERE name = ?;"),
    INSERT_CHANNEL("""
            INSERT INTO channels
            (name, canWrite, members, "type")
            VALUES(?, ?, ?, ?);"""),

    GET_USER_BY_NAME("SELECT * FROM users WHERE name = ?;"),
    GET_ALL_USERS("SELECT * FROM users;"),
    INSERT_USER("""
            INSERT INTO users
            (name, color, salt, password)
            VALUES(?, ?, ?, ?);
            """);

    private final String content;

    Statments(String content) {
        this.content = content;
    }

    @Override
    public String get() {
        return this.content;
    }
}
