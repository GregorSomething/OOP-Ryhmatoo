package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.ChannelCreateRequest;
import oop.ryhmatoo.common.socket.response.LoginResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ServerConnectionImp implements ServerConnection {

    private final String address;
    private final int port;

    private final WriteSocket writeSocket;
    private ReadSocket readSocket;
    private final JSONHelper jsonHelper;


    public ServerConnectionImp(String address) throws IllegalArgumentException {
        // Pole kõige korreksem regex kuid parem kui mitte midagi.
        if (!Pattern.matches("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d{1,})?)", address)) {
            throw new IllegalArgumentException("Aaderss pole korrektne ip aadress (, koos portiga).");
        }
        String[] addressPart = address.split(":");
        this.address = addressPart[0];
        this.port = addressPart.length == 2 ? Integer.parseInt(addressPart[1]) : 10021;

        this.jsonHelper = new JSONHelper();

        try {
            this.writeSocket = new WriteSocket(this.address, port, this.jsonHelper);
        } catch (IOException e) {
            // Suurema tõenöousesega on see et pole sellist hosti.
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public LoginResponse isValidCredentials(String name, String password) {
        try {
            return this.writeSocket.tryLogin(100, name, password);
        } catch (IOException e) {
            return new LoginResponse(false, e.getMessage());
        }
    }

    @Override
    public void createNewUser(String name, String password, String color) throws LoginException {
        try {
            LoginResponse rs = this.writeSocket.createNewUser(name, password, color);
            if (!rs.valid()) throw new LoginException(rs.message());
        } catch (IOException e) {
            e.printStackTrace(); // DEBUG eesmärgil
            throw new LoginException(e.getMessage());
        }
    }

    @Override
    public void start(String name, String password) throws LoginException {
        try {
            LoginResponse rsRead = this.writeSocket.tryLogin(103, name, password);
            this.readSocket = new ReadSocket(this.address, this.port, this.jsonHelper);
            LoginResponse rsWrite = this.readSocket.tryLogin(102, name, password);
            if (rsRead.valid() && rsWrite.valid()) {
                this.readSocket.run();
                return;
            }
            this.readSocket.close();
            this.readSocket = null;
            throw new LoginException(rsRead.message());
        } catch (IOException e) {
            throw new LoginException(e.getMessage());
        }
    }

    @Override
    public List<String> getActiveUsers() {
        try {
            return this.writeSocket.getActiveUsers();
        } catch (IOException e) {
            e.printStackTrace(); // Debug eesmärgil
            return Collections.emptyList();
        }
    }

    @Override
    public List<Channel> getChats() {
        try {
            return this.writeSocket.getChannels();
        } catch (IOException e) {
            e.printStackTrace(); // Debug eesmärgil
            return Collections.emptyList();
        }
    }

    @Override
    public List<Message> getLastMessages(int limit, String channel) throws IllegalArgumentException {
        try {
            if (limit <= 0) throw new IllegalArgumentException("Limit ei saa olla väiksem võrdne nulliga.");
            return this.writeSocket.getMessages(channel, limit);
        } catch (IOException e) {
            e.printStackTrace(); // Debug eesmärgil
            return Collections.emptyList();
        }
    }

    @Override
    public void sendMessage(String channel, String content) {
        try {
            this.writeSocket.sendMessage(
                    new Message("PoleOluline", "-", channel, content, 1, Message.Type.MESSAGE));
        } catch (IOException e) {
            // Server pole ei tokiks olla kinni kui kasutaja kirjutab.
            // Välistatud ühenduse loomisega.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendFile(String channel, File file, Message.Type type) throws IOException, IllegalArgumentException {
        Path tofile = file.toPath(); // Palju parem asi millena hoida viide failile imo
        this.writeSocket.sendFile(channel, tofile, type);
    }

    @Override
    public File getFile(Message refMessage) {
        if (refMessage.type().equals(Message.Type.MESSAGE))
            throw new RuntimeException("Message is not file type message");
        try {
            return this.writeSocket.getFile(refMessage.content());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createNewChannel(String name, List<String> members, Channel.Type type) {
        try {
            this.writeSocket.createNewChannel(new ChannelCreateRequest(name, type, members));
        } catch (IOException e) {
            // Server pole ei tokiks olla kinni kui kasutaja kirjutab.
            // Välistatud ühenduse loomisega.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerMessageListener(Consumer<Message> listener) {
        this.readSocket.registerMessageListener(listener);
    }

    @Override
    public void registerChannelListener(Consumer<Channel> listener) {
        this.readSocket.registerChannelListener(listener);
    }

    @Override
    public void close() throws IOException {
        try (this.writeSocket) {
            if (this.readSocket != null) this.readSocket.close();
        }
    }

    @Override
    public void registerUserListener(Consumer<String> listener) {
        //TODO: Implement
    }

    @Override
    public List<String> getAllUsers() {
        // TODO: Implement
        return List.of("See", "ei", "toimi", "veel");
    }

    @Override
    public void editMessage(Message original, String newContent) {
        // TODO: Implement
    }

    @Override
    public void editChannel(Channel channel) {
        // TODO: Implement
    }

    @Override
    public void deleteMessage(Message message) {
        // TODO: Implement
    }

    @Override
    public void deleteChannel(Channel channel) {
        // TODO: Implement
    }
}
