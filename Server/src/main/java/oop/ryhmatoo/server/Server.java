package oop.ryhmatoo.server;

import lombok.Getter;
import lombok.SneakyThrows;
import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.socket.SocketConnector;
import oop.ryhmatoo.server.socket.statehandlers.ReadHandler;

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

    @SneakyThrows
    public void onMessage(Message message) {
        System.out.println(message);
        this.database.getMessageStorage().saveMessage(message);
        this.sockets.getStateHandler().sendMessage(message);
    }

    @SneakyThrows
    public void onChannelCreate(Channel channel) {
        System.out.println(channel);
        this.database.getChannelStorage().saveNewChannel(channel);
        this.sockets.getStateHandler().sendChannel(channel);
    }

    public static Server getInstance() {
        return instance;
    }
}
