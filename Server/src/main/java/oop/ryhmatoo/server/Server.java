package oop.ryhmatoo.server;

import lombok.Getter;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.socket.SocketConnector;

import java.io.IOException;
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
    @Getter
    private final SocketConnector sockets;

    public Server(String[] args) throws IOException {
        Server.instance = this;
        this.executorService = Executors.newFixedThreadPool(THREADS);
        this.database = new Database("data.sqlite");
        this.sockets = new SocketConnector(10021);
    }

    public static void main(String[] args) throws IOException {
        new Server(args);
    }

    public static Server getInstance() {
        return instance;
    }
}
