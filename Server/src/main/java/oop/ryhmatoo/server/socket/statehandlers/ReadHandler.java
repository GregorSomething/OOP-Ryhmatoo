package oop.ryhmatoo.server.socket.statehandlers;

import lombok.SneakyThrows;
import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.socket.SocketConnector;
import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ReadHandler implements SocketStateHandler {

    private final JSONHelper helper;

    public ReadHandler() {
        this.helper = Server.getInstance().getJSONHelper();
    }

    @Override
    public boolean handel(int code, SocketHolder socket) {
        return false; // Selle pole Hetkel koode.
    }

    public boolean sendChannel(Channel channel) {
        Server.getInstance().getSockets().getSockets().stream()
                .filter(s -> s.getState().equals(SocketHolder.State.READING_SOCKET))
                .filter(s -> channel.members().contains(s.getUser().name()))
                .forEach(s -> this.write(221, s, channel));

        return true;
    }

    public boolean sendMessage(Message message) {
        try {
            Channel messageChannel = Server.getInstance().getDatabase().getChannelStorage().getChannelByName(message.channel());
            Server.getInstance().getSockets().getSockets().stream()
                    .filter(s -> s.getState().equals(SocketHolder.State.READING_SOCKET))
                    .filter(s -> messageChannel == null || messageChannel.members().contains(s.getUser().name()))
                    .forEach(s -> this.write(220, s, message));

            return true;
        } catch (SQLException e) {
            Server.LOG.warning("Viga response loomisel. sendMessage " + e.getMessage());
            return false;
        }
    }

    public boolean sendUser(String user) {
        Server.getInstance().getSockets().getSockets().stream()
                .filter(s -> s.getState().equals(SocketHolder.State.READING_SOCKET))
                .forEach(s -> this.write(222, s, user));

        return true;
    }

    @SneakyThrows
    private void write(int code, SocketHolder socket, Object object) {
        DataOutputStream dos = socket.getDataOutputStream();
        dos.writeInt(code);
        dos.writeUTF(this.helper
                .getMapper().writeValueAsString(object));
    }
}
