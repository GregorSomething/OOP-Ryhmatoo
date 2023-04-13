package oop.ryhmatoo.server;

import lombok.Getter;
import oop.ryhmatoo.server.data.Database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int THREADS = 10;

    private static Server instance;
    // Instance varibles
    @Getter
    private final Database database;
    @Getter
    private final ExecutorService executorService;

    public Server(String[] args) {
        Server.instance = this;
        this.executorService = Executors.newFixedThreadPool(THREADS);
        this.database = new Database("data.sqlite");
    }

    public static void main(String[] args) {
        new Server(args);
    }

    public static Server getInstance() {
        return instance;
    }
}
