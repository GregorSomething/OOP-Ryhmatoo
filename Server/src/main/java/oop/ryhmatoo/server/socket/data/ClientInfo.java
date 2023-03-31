package oop.ryhmatoo.server.socket.data;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Andmeklass mis kajastab info mille client saadab alguses kohe.
 */
public record ClientInfo(String username, String displayName, String password) {

    public static ClientInfo from(DataInputStream dis) throws IOException {
        String name = dis.readUTF();
        String displayName = dis.readUTF();
        String password = dis.readUTF();
        return new ClientInfo(name, displayName, password);
    }
}
