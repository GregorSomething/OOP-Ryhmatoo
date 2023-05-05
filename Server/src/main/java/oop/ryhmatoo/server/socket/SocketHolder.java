package oop.ryhmatoo.server.socket;

import lombok.Getter;
import lombok.Setter;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.data.records.ServerUser;
import oop.ryhmatoo.server.socket.statehandlers.StateHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHolder {

    private final Socket socket;
    @Setter @Getter
    private State state = State.CONNECTED;
    @Setter @Getter
    private ServerUser user = null;

    @Getter
    private final DataInputStream dataInputStream;
    @Getter
    private final DataOutputStream dataOutputStream;

    private final StateHandler stateHandler;

    public SocketHolder(Socket socket) throws IOException {
        this.socket = socket;
        Server.getInstance().getSockets().getSockets().add(this); // Lisab ennast socketite nimekirja
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.stateHandler = Server.getInstance().getSockets().getStateHandler();
        this.start();
    }

    public void start() throws IOException {
        try (this.dataInputStream; this.dataOutputStream) {
            while (this.socket.isConnected() && !this.socket.isClosed()) {
                this.update();
            }
        }
        this.state = State.CLOSED;
        Server.getInstance().getSockets().getSockets().remove(this);
    }

    public void close() throws IOException {
        this.socket.close();
        Server.LOG.info(String.format("Socket %s disconnectis serverist, nimi: %s, mode: %s.",
                socket.toString(), this.user.name(), this.state));
        this.state = State.CLOSED;
        Server.getInstance().getSockets().getSockets().remove(this);
    }

    public void update() throws IOException {
        // Loe request code
        int code = this.dataInputStream.readInt();
        if (code == 109) this.close(); // Close code
        // Saada see kuhugi käsitlusse
        this.stateHandler.handel(code, this);
    }

    public enum State {
        CONNECTED,
        READING_SOCKET,
        WRITING_SOCKET,
        CLOSED;
    }

    @Override
    public String toString() {
        return "SocketHolder{" +
                "socket=" + socket +
                ", state=" + state +
                ", username='" + user + '\'' +
                '}';
    }
}
