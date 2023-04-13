package oop.ryhmatoo.server.socket;

import java.io.IOException;
import java.net.Socket;

public class SocketHolder {

    private final Socket socket;
    private State state = State.CONNECTED;

    public SocketHolder(Socket socket) {
        this.socket = socket;
        this.start();
    }

    public void start() {
        while (this.socket.isConnected() && !this.socket.isClosed()) {
            this.update();
        }
        this.state = State.CLOSED;
    }

    public void close() throws IOException {
        this.socket.close();
        this.state = State.CLOSED;
    }

    public void update() {

    }

    public enum State {
        CONNECTED,
        LOGGED_IN,
        READING_SOCKET,
        WRITING_SOCKET,
        CLOSED;
    }
}
