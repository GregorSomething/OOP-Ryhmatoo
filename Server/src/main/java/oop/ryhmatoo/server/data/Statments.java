package oop.ryhmatoo.server.data;

public enum Statments implements SQLStatement {
    GET_ALL_MESSAGES("SELECT * FROM messages;"),
    INSERT_MESSAGE("INSERT INTO messages(sender, channel, message, time) VALUES(?, ?, ?, ?);");

    private final String content;

    Statments(String content) {
        this.content = content;
    }

    @Override
    public String get() {
        return this.content;
    }
}
