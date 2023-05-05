package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.ChannelCreateRequest;
import oop.ryhmatoo.common.socket.request.CreateNewUserRequest;
import oop.ryhmatoo.common.socket.request.MessageRequest;
import oop.ryhmatoo.common.socket.response.LoginResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WriteSocket extends AbstractSocketConnection {

    private final FileHandler fileHandler;

    public WriteSocket(String address, int port, JSONHelper jsonHelper) throws IOException {
        super(address, port, jsonHelper);
        fileHandler = new FileHandler();
    }

    public LoginResponse createNewUser(String name, String color, String password) throws IOException {
        CreateNewUserRequest request = new CreateNewUserRequest(name, color, password);
        this.dataOutputStream.writeInt(101);
        this.dataOutputStream.writeUTF(this.jsonHelper.getMapper().writeValueAsString(request));
        int resCode = this.dataInputStream.readInt();
        return this.jsonHelper.readObjectFrom(this.dataInputStream, LoginResponse.class);
    }

    public List<String> getActiveUsers() throws IOException {
        this.dataOutputStream.writeInt(111);
        int resCode = this.dataInputStream.readInt();
        return this.jsonHelper.getListFromJSON(
                this.dataInputStream.readUTF(), String.class);
    }

    public List<Channel> getChannels() throws IOException {
        this.dataOutputStream.writeInt(110);
        int resCode = this.dataInputStream.readInt();
        return this.jsonHelper.getListFromJSON(
                this.dataInputStream.readUTF(), Channel.class);
    }

    public List<Message> getMessages(String channel, int limit) throws IOException {
        this.dataOutputStream.writeInt(112);
        this.dataOutputStream.writeUTF(this.jsonHelper.getMapper()
                .writeValueAsString(
                        new MessageRequest(channel, limit)));
        int resCode = this.dataInputStream.readInt();
        return this.jsonHelper.getListFromJSON(
                this.dataInputStream.readUTF(), Message.class);
    }

    public void sendMessage(Message message) throws IOException {
        this.dataOutputStream.writeInt(120);
        this.dataOutputStream.writeUTF(this.jsonHelper
                .getMapper().writeValueAsString(message));
    }

    public void createNewChannel(ChannelCreateRequest request) throws IOException {
        this.dataOutputStream.writeInt(121);
        this.dataOutputStream.writeUTF(this.jsonHelper.getMapper().writeValueAsString(request));
    }

    public void sendFile(String channle, Path path, Message.Type type) throws IllegalArgumentException, IOException {
        if (type.equals(Message.Type.MESSAGE))
            throw new IllegalArgumentException("Use sendMessage instead.");
        this.fileHandler.send(channle, path, type, this.dataOutputStream, this.jsonHelper);
    }

    public File getFile(String storageName) throws IOException {
        return this.fileHandler.getFile(storageName, this.dataInputStream, this.dataOutputStream);
    }
}
