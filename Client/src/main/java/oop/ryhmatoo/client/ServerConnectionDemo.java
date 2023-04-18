package oop.ryhmatoo.client;

import oop.ryhmatoo.client.socket.ServerConnection;

import java.io.IOException;
import java.sql.SQLException;

public class ServerConnectionDemo {

    public static void main(String[] args) {
        // Try-with-resource, et sulguks. Luua ainult üks ühendus
        try (ServerConnection server = ServerConnection.connect("127.0.0.1:10021")) {
            // Kontrollid parooli, kuid see ei logi sisse
            System.out.println(server.isValidCredentials("test", "parool1"));
            // Võimaldab edasise suhtluse, kasuta ainult üks kord.
            server.start("test", "parool1"); // Logib sisse ka.

            // Registreerib kuulajad, neid võib olla mitmeid.
            server.registerMessageListener(System.out::println);
            server.registerChannelListener(System.out::println);

            //server.createNewChannel("KanaJama", List.of("test", "Gregor", "kanad"), Channel.Type.CHANNEL);
            System.out.println(server.getChats()); // Võtab kanalid
            System.out.println(server.getActiveUsers()); // Hetkel aktiivsed kasutajad
            System.out.println("saatmine: ");
            server.sendMessage("KanaJama", "Terekest cliendilt");
            server.sendMessage("KanaJama", "Terekest cliendilt2");
            server.sendMessage("KanaJama", "Terekest cliendilt3");
            System.out.println("Sõnumid:");
            System.out.println(server.getLastMessages(10, "KanaJama"));

        } catch (ServerConnection.LoginException loginException) {
            // Käsitle login vigu siin
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
