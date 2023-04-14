package oop.ryhmatoo.server.socket;

import lombok.Getter;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.socket.statehandlers.StateHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketConnector {

    private final ServerSocket socket;
    private final Thread runningOn;
    @Getter
    private final StateHandler stateHandler;
    @Getter
    private final List<SocketHolder> sockets;

    public SocketConnector(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.stateHandler = new StateHandler();
        // Võiks sobida asünkoroonsete op.ide jaoks
        this.sockets = Collections.synchronizedList(new ArrayList<SocketHolder>());
        this.runningOn = new Thread(this::update);
        this.runningOn.start();
    }

    private void update() {
        while (true) {
            try {
                this.handelNewConnection(socket.accept());
            } catch (IOException e) {
                if (this.socket.isClosed()) {
                    this.runningOn.interrupt();
                    System.out.println("ServerSocket is closed, terminating server stuff.");
                    return;
                }
            }
        }
    }

    private void handelNewConnection(Socket socket) {
        Server.getInstance().getExecutorService()
                .submit(() -> new SocketHolder(socket));
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
