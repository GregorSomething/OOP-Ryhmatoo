package oop.ryhmatoo.client.socket;

import java.io.ObjectInputStream;

/**
 * Andmeklass mis kirjeldab cliendi infot.
 * @param name username
 * @param displayName displayname
 * @param address server address
 * @param port server port
 * @param password user password
 */
public record ClientInfo(String name, String displayName,
                         String address, int port, String password) {
}
