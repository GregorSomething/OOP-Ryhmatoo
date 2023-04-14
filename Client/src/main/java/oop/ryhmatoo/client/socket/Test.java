package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Channel;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        try (ServerConnection server = ServerConnection.connect("127.0.0.1:10021")) {
            System.out.println(server.isValidCredentials("test", "parool1"));
            server.start("test", "parool1"); // Logib sisse ka.

            server.registerMessageListener(System.out::println);
            server.registerChannelListener(System.out::println);

            //server.createNewChannel("KanaJama", List.of("test", "Gregor", "kanad"), Channel.Type.CHANNEL);
            System.out.println(server.getChats());
            System.out.println(server.getActiveUsers());
            System.out.println("saatmine: ");
            server.sendMessage("KanaJama", "Terekest cliendilt");
            server.sendMessage("KanaJama", "Terekest cliendilt2");
            server.sendMessage("KanaJama", "Terekest cliendilt3");
            System.out.println("Sõnumid:");
            System.out.println(server.getLastMessages(10, "KanaJama"));

        }
    }
}
