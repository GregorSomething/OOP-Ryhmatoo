package oop.ryhmatoo.client.socket;

import lombok.SneakyThrows;
import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReadSocket extends AbstractSocketConnection {

    private final List<Consumer<Message>> messageListeners;
    private final List<Consumer<Channel>> channelListeners;
    private final List<Consumer<String>> userListeners;

    public ReadSocket(String address, int port, JSONHelper jsonHelper) throws IOException {
        super(address, port, jsonHelper);
        this.messageListeners = new ArrayList<>();
        this.channelListeners = new ArrayList<>();
        this.userListeners = new ArrayList<>();
    }

    public void registerMessageListener(Consumer<Message> listener) {
        this.messageListeners.add(listener);
    }

    public void registerChannelListener(Consumer<Channel> listener) {
        this.channelListeners.add(listener);
    }

    public void registerUserListener(Consumer<String> listener) {
        this.userListeners.add(listener);
    }

    public void run() {
        new Thread(this::start).start();
    }

    @SneakyThrows
    private void start() {
        try {
            while (!this.socket.isClosed()) {
                this.update();
            }
        } catch (SocketException ignored) {
        }
    }

    public void update() throws IOException {
        int code = this.dataInputStream.readInt();
        switch (code) {
            case 220 -> {
                Message message = this.jsonHelper.readObjectFrom(this.dataInputStream, Message.class);
                this.messageListeners.forEach(l -> l.accept(message));
            }
            case 221 -> {
                Channel channel = this.jsonHelper.readObjectFrom(this.dataInputStream, Channel.class);
                this.channelListeners.forEach(l -> l.accept(channel));
            }
            case 222 -> {
                String user = dataInputStream.readUTF();
                this.userListeners.forEach(l -> l.accept(user));
            }
        }
    }
}
