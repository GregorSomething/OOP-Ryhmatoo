package oop.ryhmatoo.client;

import oop.ryhmatoo.client.socket.ClientInfo;
import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.client.socket.ServerConnectionImp;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Tere tulemast chati!");
        ServerConnection server = new ServerConnectionImp();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Sisesta kasutajanimi");
            String name = sc.nextLine();
            System.out.println("Sisesta serveri aadress vormis ip:port");
            String address = sc.nextLine();
            int port = Integer.parseInt(address.split(":")[1]);
            String ip = address.split(":")[0];
            System.out.println("Sisesta parool");
            String password = sc.nextLine();
            ClientInfo info = new ClientInfo(name, name, ip, port, password);
            try {
                server.start(info);
            } catch (ServerConnection.LoginException e) {
                // TODO väljasta see
                System.out.println(e.getMessage());
                continue;
            }
            break;
        }
        while (true) {
            System.out.println("Kas tahad sõnumi saata või viimast sõnumit lugeda? (s/l/exit)");
            String input = sc.nextLine();
            switch (input) {
                case "s":
                    System.out.println("Sisesta sõnum");
                    String message = sc.nextLine();
                    System.out.println(server.sendMessage(message, "K1"));
                case "l":
                    System.out.println(server.getLastMessages(100));
                case "exit":
                    break;
                default:
                    System.out.println("Sisesta s, l või exit");
            }
        }
    }
}