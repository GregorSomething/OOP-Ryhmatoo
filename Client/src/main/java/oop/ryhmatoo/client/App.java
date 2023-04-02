package oop.ryhmatoo.client;

import oop.ryhmatoo.client.socket.ClientInfo;
import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.client.socket.ServerConnectionImp;
import oop.ryhmatoo.common.data.Message;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
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
            if(input.equals("s")){
                System.out.println("Sisesta sõnum");
                String message = sc.nextLine();
                int status = server.sendMessage(message, "K1").status();
                if(status == 1){
                    System.out.println("Sõnum saadetud");
                } else {
                    System.out.println("Sõnumi saatmine ebaõnnestus");
                }
            } else if(input.equals("l")){
                List<Message> messages = server.getLastMessages(1);
                if(messages == null || messages.size() == 0){
                    System.out.println("Sõnumite lugemine ebaõnnestus või pole sõnumit mida lugeda");
                } else {
                    ZonedDateTime zonedDateTime = ZonedDateTime
                            .ofInstant(Instant
                                    .ofEpochSecond(messages.get(0).timestamp()), ZoneId.systemDefault());
                    System.out.println("[" + zonedDateTime.toLocalTime() + " " + zonedDateTime.toLocalDate() + "] " + messages.get(0).sender() + ": " + messages.get(0).content());
                }
            } else if(input.equals("exit")){
                break;
            } else {
                System.out.println("Sisesta s, l või exit");
            }
        }
        System.exit(0);
    }
}