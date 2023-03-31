package oop.ryhmatoo.server.socket;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.data.ResponseHeader;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.socket.data.ClientInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private ClientInfo info;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (this.socket;
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            // Loeb algse client info.
            try {
                info = ClientInfo.from(dis);
            } catch (IOException e) {
                ResponseHeader rs = new ResponseHeader(0,
                        "Client info lugemine ei õnnestunud: " + e.getMessage());
                rs.write(dos);
                e.printStackTrace(); // Debuggimise huvides
                return;
            }

            // Kontrollib parooli
            if (!Server.getInstance().getAuthService().hasCorrectPassword(info)) {
                ResponseHeader rs = new ResponseHeader(0,
                        "Vale parool!");
                rs.write(dos);
                return;
            }
            // Tagastan info
            ResponseHeader rs = new ResponseHeader(1,
                    "Sisse logitud");
            rs.write(dos);
            System.out.printf("Client ühendus: name %s, displayName: %s%n", info.username(), info.displayName());

            // Kuula selle clienti soove
            while (true) {
                // Vaatab kas on midagi lugeda
                if (dis.available() < 4) {
                    Thread.sleep(100);
                    continue;
                }

                // Loeb request koodi ja käsitleb seda
                int code = dis.readInt();
                switch (code) {
                    case 100 -> this.readMessage(dis, dos);
                    case 110 -> this.onMessageRequest(dis, dos);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // Käsitle seda, kuid tühistati kuulamine.
            throw new RuntimeException(e);
        }
    }

    // Client 100 code tegevus
    public void readMessage(DataInputStream dis, DataOutputStream dos) throws IOException {
        try {
            // Loen infos
            String message = dis.readUTF();
            String channel = dis.readUTF();
            long timestamp = new Date().getTime() / 1000;
            Message msg = new Message(this.info.displayName(), channel, message, timestamp);
            Server.getInstance().onMessage(this.info, msg); // Edastan info
            ResponseHeader rs = new ResponseHeader(1,
                    "OK");
            rs.write(dos);
        } catch (IOException e) {
            ResponseHeader rs = new ResponseHeader(0,
                    "Lugemise viga: " + e.getMessage());
            e.printStackTrace(); // Debuggimise huvides
            rs.write(dos);
        }
    }

    public void onMessageRequest(DataInputStream dis, DataOutputStream dos) throws IOException {
        try {
            // Loen infos
            // TODO: Asenda korrektes asjaga
            int limit = dis.readInt();
            List<Message> messages = getDummyMessages(limit);

            // saada response sest ma teen nii?
            ResponseHeader rs = new ResponseHeader(1,
                    "OK");
            rs.write(dos);

            // Saada sisu
            dos.writeInt(messages.size());
            for (Message m : messages) {
                m.write(dos);
            }

        } catch (IOException e) {
            ResponseHeader rs = new ResponseHeader(0,
                    "Lugemise viga: " + e.getMessage());
            e.printStackTrace(); // Debuggimise huvides
            rs.write(dos);
        }
    }

    public static List<Message> getDummyMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Gregor1", "K1", "See on mega hea storage", 1));
        messages.add(new Message("Gregor1", "K1", "tegelt vist mitte", 2));
        messages.add(new Message("Gregor3", "K1", "päriselt v?", 3));
        messages.add(new Message("Gregor2", "K1", "mul pole lihtsalt sõnu, ta lihtsalt ignob limiiti", 4));
        return messages;
    }
}

