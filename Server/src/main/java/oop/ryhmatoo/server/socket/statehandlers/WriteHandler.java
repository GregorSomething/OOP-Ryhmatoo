package oop.ryhmatoo.server.socket.statehandlers;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.ChannelCreateRequest;
import oop.ryhmatoo.common.socket.request.MessageRequest;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.data.Database;
import oop.ryhmatoo.server.data.FileStorage;
import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
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
            case 113 -> this.handleFileRequest(socket);
            case 120 -> this.handelMessageReceive(socket);
            case 121 -> this.handelChannelCreateRequest(socket);
            case 122 -> this.handelFileReceive(socket);
            case 124 -> this.handelAllUserRequest(socket);
            default -> throw new IllegalStateException("Unexpected request code: " + code);
        };
    }

    private boolean handelChannelRequest(SocketHolder socket) {
        try {
            DataOutputStream dos = socket.getDataOutputStream();
            dos.writeInt(210);
            dos.writeUTF(this.helper.getListAsJSON(
                    this.database.getChannelStorage()
                            .getChannelsForUser(socket.getUser().name())));
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
                    .map(s -> s.getUser().name()).distinct()
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
            // Korrigeerin sõnumi et kasutaja ei saaks valetada nime v aja osas.
            Message messageCorrect = new Message(socket.getUser().name(), socket.getUser().color(),
                    message.channel(), message.content(), new Date().getTime(), message.type());
            Server.getInstance().onMessage(messageCorrect);
            return true;
        } catch (IOException e) {
            System.out.println("Viga requesti käsitlemisel. handelMessageReceive " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelFileReceive(SocketHolder socket) {
        try {
            Message message = helper.readObjectFrom(socket.getDataInputStream(), Message.class);
            int len = socket.getDataInputStream().readInt();
            byte[] data = socket.getDataInputStream().readNBytes(len);

            String newFileName = FileStorage.write(message.channel(), message.content(), data);
            Message messageCorrect = new Message(socket.getUser().name(), socket.getUser().color(),
                    message.channel(), newFileName, new Date().getTime(), message.type());

            Server.getInstance().onMessage(messageCorrect);
            return true;
        } catch (IOException e) {
            System.out.println("Viga requesti käsitlemisel. handelFileReceive " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handleFileRequest(SocketHolder socket) {
        try {
            String name = socket.getDataInputStream().readUTF();
            byte[] data = FileStorage.read(name);
            DataOutputStream dos = socket.getDataOutputStream();
            dos.writeInt(213);
            dos.writeInt(data.length);
            dos.write(data);
            return true;
        } catch (IOException e) {
            System.out.println("Viga requesti käsitlemisel. handleFileRequest " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelChannelCreateRequest(SocketHolder socket) {
        try {
            ChannelCreateRequest createRequest = helper.readObjectFrom(socket.getDataInputStream(), ChannelCreateRequest.class);
            // Kontrolli kas see on olemas
            Channel existingChannel = this.database.getChannelStorage().getChannelByName(createRequest.name());
            if (existingChannel != null) return true;

            Channel channel = new Channel(createRequest.name(), true, createRequest.members(), createRequest.type());
            Server.getInstance().onChannelCreate(channel);
            return true;
        } catch (IOException | SQLException e) {
            System.out.println("Viga requesti käsitlemisel. handelChannelCreateReceive " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }

    private boolean handelAllUserRequest(SocketHolder socket) {
        try {
            DataOutputStream dos = socket.getDataOutputStream();
            dos.writeInt(214);
            // TODO
            dos.writeUTF(this.helper.getListAsJSON(this.database.getUserStorage().getAllUsernames()));
            return true;
        } catch (IOException | SQLException e) {
            System.out.println("Viga requesti käsitlemisel. handelActiveUserRequest " + e.getMessage());
            e.printStackTrace(); // Nii saan kiiremini debuggida
            return false;
        }
    }
}
