package oop.ryhmatoo.server;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.socket.AuthService;
import oop.ryhmatoo.server.socket.SocetMain;
import oop.ryhmatoo.server.socket.data.ClientInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serveri main klass
 */
public class Server {
    private static final int PORT = 10021; // Asenda millegi muuga, see siia?

    private static Server instance;
    // Executor service serverile
    public final ExecutorService executor;
    public final AuthService authService;
    public final SocetMain socetMain;
    private HashMap<String, String> logins;

    public Server(String[] args) throws IOException {
        Server.instance = this;
        this.executor = Executors.newFixedThreadPool(8);
        this.authService = new AuthService();
        this.socetMain = new SocetMain(PORT);
        logins = readLoginsFromFile();
        // Stardib socketite kuulamise
        socetMain.start();
    }

    private HashMap<String,String> readLoginsFromFile() throws FileNotFoundException {
        HashMap<String,String> logins = new HashMap<>();
        try(Scanner scanner = new Scanner(new File("logins.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                logins.put(parts[0], parts[1]);
            }
        }
        return logins;
    }
    public static void main(String[] args) throws IOException {
        Server.instance = new Server(args);
    }

    public void onMessage(ClientInfo client, Message message) {
        // TODO: Asenda mõistlikuma asjaga :)
        System.out.printf("[%s] %s - %s%n", client.displayName(), message.channel(), message.content());
    }

    /**
     * Tagastab serveri olemi
     * @return selle instance
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * @return ExecutorService et jooksutada asju async
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Autentimisega tegelev klass
     * @return tagastab selle olemi
     */
    public AuthService getAuthService() {
        return authService;
    }
}
