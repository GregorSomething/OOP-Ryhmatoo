package oop.ryhmatoo.client;

import oop.ryhmatoo.client.socket.ClientInfo;
import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.client.socket.ServerConnectionImp;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException, InterruptedException {
        ServerConnection server = new ServerConnectionImp();
        ClientInfo info = new ClientInfo("test", "Testi", "127.0.0.1", 10021,"parool1");
        server.start(info);
        Thread.sleep(100);
        System.out.println("Siin");
        System.out.println(server.sendMessage("Testin asju", "K1"));
        System.out.println(server.getLastMessages(100));
    }
}
