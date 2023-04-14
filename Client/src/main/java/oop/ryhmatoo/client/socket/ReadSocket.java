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

    public ReadSocket(String address, int port, JSONHelper jsonHelper) throws IOException {
        super(address, port, jsonHelper);
        this.messageListeners = new ArrayList<>();
        this.channelListeners = new ArrayList<>();
    }

    public void registerMessageListener(Consumer<Message> listener) {
        this.messageListeners.add(listener);
    }

    public void registerChannelListener(Consumer<Channel> listener) {
        this.channelListeners.add(listener);
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
        } catch (SocketException e) {
            System.out.println();
            System.out.println("See pole error! Vaid asi millest ei saanud lahti. Korrektne sulgemine tehti, see põhjustab selle");
            e.printStackTrace(); // Ma ei suutnud seda lahendada;
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
        }
    }
}
