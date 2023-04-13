package oop.ryhmatoo.server.socket;

import oop.ryhmatoo.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnector {

    private final ServerSocket socket;
    private final Thread runningOn;

    public SocketConnector(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.runningOn = new Thread(this::update);
        this.runningOn.start();
    }

    public void update() {
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

    public void handelNewConnection(Socket socket) {
        Server.getInstance().getExecutorService()
                .submit(() -> new SocketHolder(socket));
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
