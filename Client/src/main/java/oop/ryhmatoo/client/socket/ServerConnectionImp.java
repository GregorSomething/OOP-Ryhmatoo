package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.data.ResponseHeader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerConnectionImp implements ServerConnection, Runnable {
    private boolean working = false;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ClientInfo info;
    private ResponseHeader lastResponse;


    @Override
    public void start(ClientInfo info) throws LoginException {
        this.info = info;
        new Thread(this).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (lastResponse != null && lastResponse.status() != 1)
            throw new LoginException("Login failed (" + lastResponse.status() + "): " + lastResponse.message());
    }

    /**
     * Saadab sõnumi serveri, server teab kasutajanime
     * @param message sõnumi sisu mis saata
     * @param channel kanal kust see tuli
     * @return Vastus serverilt
     * @throws IOException on error
     */
    @Override
    public ResponseHeader sendMessage(String message, String channel) throws IOException {
        if (!working) return null;
        synchronized (this) {
            dos.writeInt(100);
            dos.writeUTF(message);
            dos.writeUTF(channel);
            return ResponseHeader.from(dis);
        }
    }

    /**
     * Saa viimased x sõnumit
     * @param limit mitu sõnumit küsida
     * @return sõnumid, tegelik suurus võib erineda, võib olla null
     * @throws IOException vea korral
     */
    @Override
    public List<Message> getLastMessages(int limit) throws IOException {
        if (!working) return null;
        synchronized (this) {
            dos.writeInt(110);
            dos.writeInt(limit);
            ResponseHeader rs = ResponseHeader.from(dis);
            if (rs.status() == 0) {
                throw new IOException(rs.message());
            }
            int count = dis.readInt();
            List<Message> messages = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                messages.add(Message.from(dis));
            }
            return messages;
        }
    }

    public List<String> getLogins() throws IOException {
        if (!working) return null;
        synchronized (this) {
            dos.writeInt(120);
            ResponseHeader rs = ResponseHeader.from(dis);
            if (rs.status() == 0) throw new IOException(rs.message());

            int count = dis.readInt();
            List<String> logins = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                logins.add(dis.readUTF());
            }
            return logins;
        }
    }
    @Override
    public void registerMessageListener(Consumer<Message> listener) {
        throw new RuntimeException("Ma pole seda veel teinud - Gregor");
    }

    @Override
    public void run() {
        this.working = true;
        try (Socket socket = new Socket(this.info.address(), this.info.port())) {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            // Send info
            dos.writeUTF(this.info.name());
            dos.writeUTF(this.info.displayName());
            dos.writeUTF(this.info.password());
            ResponseHeader rs = ResponseHeader.from(dis);
            this.lastResponse = rs;
            if (rs.status() != 1) {
                // Kui sisselogimine polnud edukas.
                this.working = false;
                return;
            }
            // TODO tee asju
            while (true) {
                Thread.sleep(100);
            }

        } catch (IOException | InterruptedException e) {
            this.lastResponse = new ResponseHeader(0, e.getClass().getName() + ": " + e.getMessage());
            //throw new RuntimeException(e);
        } finally {
            this.working = false;
            // Kuna neid ei saanud auto sulgeda.
            try {
                if (this.dis != null)
                    this.dis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (this.dos != null)
                    this.dos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
