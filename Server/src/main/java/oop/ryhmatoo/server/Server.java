package oop.ryhmatoo.server;

import lombok.Getter;
import oop.ryhmatoo.server.data.Database;

public class Server {
    private static Server instance;
    // Instance varibles
    @Getter
    private final Database database;

    public Server(String[] args) {
        Server.instance = this;
        this.database = new Database("data.sqlite");
    }

    public static void main(String[] args) {
        new Server(args);
    }

    public static Server getInstance() {
        return instance;
    }
}
