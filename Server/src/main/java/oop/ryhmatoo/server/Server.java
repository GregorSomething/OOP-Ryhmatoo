package oop.ryhmatoo.server;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.socket.AuthService;
import oop.ryhmatoo.server.socket.SocetMain;
import oop.ryhmatoo.server.socket.data.ClientInfo;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serveri main klass
 */
public class Server {
    private static final int port = 10021; // Asenda millegi muuga, see siia?

    private static Server instance;
    // Executor service serverile
    public final ExecutorService executor;
    public final AuthService authService;
    public final SocetMain socetMain;

    public Server(String[] args) throws IOException {
        Server.instance = this;
        this.executor = Executors.newFixedThreadPool(8);
        this.authService = new AuthService();
        this.socetMain = new SocetMain(port);
        // Stardib socketite kuulamise
        socetMain.start();
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
