package oop.ryhmatoo.server;

import lombok.Getter;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
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
    @Getter
    private final JSONHelper JSONHelper;

    public Server(String[] args) throws IOException {
        Server.instance = this;
        this.executorService = Executors.newFixedThreadPool(THREADS);
        this.database = new Database("data.sqlite");
        this.JSONHelper = new JSONHelper();
        this.sockets = new SocketConnector(10021);
    }

    public static void main(String[] args) throws IOException {
        new Server(args);
    }

    public void onMessage(Message message) {
        System.out.println(message);
    }

    public static Server getInstance() {
        return instance;
    }
}
