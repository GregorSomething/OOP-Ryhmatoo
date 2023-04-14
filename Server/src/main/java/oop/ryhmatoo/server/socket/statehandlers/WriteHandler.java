package oop.ryhmatoo.server.socket.statehandlers;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.MessageRequest;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class WriteHandler implements SocketStateHandler {

    private final JSONHelper helper;
    private final Database database;

    public WriteHandler() {
        this.helper = Server.getInstance().getJSONHelper();
        this.database = Server.getInstance().getDatabase();
    }

    @Override
    public boolean handel(int code, SocketHolder socket) {
        return switch (code) {
            case 110 -> this.handelChannelRequest(socket);
            case 112 -> this.handelMessageRequest(socket);
            case 111 -> this.handelActiveUserRequest(socket);
            case 120 -> this.handelMessageReceive(socket);
            default -> throw new IllegalStateException("Unexpected request code: " + code);
        };
    }

    private boolean handelChannelRequest(SocketHolder socket) {
        try {
            DataOutputStream dos = socket.getDataOutputStream();
            dos.writeInt(210);
            dos.writeUTF(this.helper.getListAsJSON(
                    this.database.getChannelStorage()
                            .getChannelsForUser(socket.getUsername())));
            return true;
        } catch (IOException | SQLException e) {
            System.out.println("Viga requesti käsitlemisel. handelChannelRequest " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelMessageRequest(SocketHolder socket) {
        try {
            MessageRequest request = helper.readObjectFrom(socket.getDataInputStream(), MessageRequest.class);
            DataOutputStream dos = socket.getDataOutputStream();

            dos.writeInt(212);
            dos.writeUTF(helper.getListAsJSON(database.getMessageStorage()
                    .getMessagesInChannelLimit(request.channel(), request.limit())));

            return true;
        } catch (IOException | SQLException e) {
            System.out.println("Viga requesti käsitlemisel. handelMessageRequest " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelActiveUserRequest(SocketHolder socket) {
        try {
            DataOutputStream dos = socket.getDataOutputStream();
            dos.writeInt(211);
            dos.writeUTF(this.helper.getListAsJSON(Server.getInstance().getSockets()
                    .getSockets().stream()
                    .filter(s -> s.getState().equals(SocketHolder.State.READING_SOCKET)
                            || s.getState().equals(SocketHolder.State.WRITING_SOCKET))
                    .map(SocketHolder::getUsername).distinct()
                    .collect(Collectors.toList())));
            return true;
        } catch (IOException e) {
            System.out.println("Viga requesti käsitlemisel. handelActiveUserRequest " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelMessageReceive(SocketHolder socket) {
        try {
            Message message = helper.readObjectFrom(socket.getDataInputStream(), Message.class);
            Server.getInstance().onMessage(message);
            return true;
        } catch (IOException e) {
            System.out.println("Viga requesti käsitlemisel. handelMessageReceive " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }
}
