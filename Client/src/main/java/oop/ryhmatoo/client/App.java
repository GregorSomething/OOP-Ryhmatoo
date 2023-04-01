package oop.ryhmatoo.client;

import oop.ryhmatoo.client.socket.ClientInfo;
import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.client.socket.ServerConnectionImp;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException, InterruptedException {
        System.out.println("Tere tulemast chati!");
        ServerConnection server = new ServerConnectionImp();
        label:
        while(true) {
            System.out.println("Kas soovite sisse logida või luua uue kasutaja? (login/create/exit)");
            try (Scanner sc = new Scanner(System.in)) {
                String input = sc.nextLine();
                switch (input) {
                    case "login": {
                        System.out.println("Sisesta kasutajanimi");
                        String name = sc.nextLine();
                        System.out.println("Sisesta serveri aadress vormis ip:port");
                        String address = sc.nextLine();
                        int port = Integer.parseInt(address.split(":")[1]);
                        String ip = address.split(":")[0];
                        System.out.println("Sisesta parool");
                        String password = sc.nextLine();
                        ClientInfo info = new ClientInfo(name, name, address, port, password);
                        server.start(info);
                        System.out.println("Sisesta sõnum");
                        String message = sc.nextLine();
                        System.out.println("Sisesta kanal");
                        String channel = sc.nextLine();
                        System.out.println(server.sendMessage(message, channel));
                        System.out.println(server.getLastMessages(100));
                        break;
                    }
                    case "create": {
                        System.out.println("Sisesta kasutajanimi");
                        String name = sc.nextLine();
                        System.out.println("Sisesta kuvatav nimi");
                        String displayName = sc.nextLine();
                        System.out.println("Sisesta serveri aadress vormis ip:port");
                        String address = sc.nextLine();
                        int port = Integer.parseInt(address.split(":")[1]);
                        String ip = address.split(":")[0];
                        System.out.println("Sisesta parool");
                        String password = sc.nextLine();
                        ClientInfo info = new ClientInfo(name, displayName, address, port, password);
                        server.start(info);
                        System.out.println("Sisesta sõnum");
                        String message = sc.nextLine();
                        System.out.println("Sisesta kanal");
                        String channel = sc.nextLine();
                        System.out.println(server.sendMessage(message, channel));
                        System.out.println(server.getLastMessages(100));
                        break;
                    }
                    case "exit":
                        break label;
                    default:
                        System.out.println("Vale sisend");
                        break;
                }
            }
        }
    }

}
