package oop.ryhmatoo.server.socket;

import oop.ryhmatoo.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

public class SocetMain implements Runnable {
    private final ServerSocket server;
    private Thread thisInstance;

    private ExecutorService executor = Server.getInstance().getExecutor();

    public SocetMain(int port) throws IOException {
        System.out.println("Starting socketMain");
        this.server = new ServerSocket(port);
    }

    public void start() {
        this.thisInstance = new Thread(this);
        this.thisInstance.start();
    }

    public void stop() throws IOException {
        this.thisInstance.interrupt();
        this.server.close();
    }

    @Override
    public void run() {
        System.out.println("Socket server is now active");
        while (true) {
            try {
                executor.submit(new ClientHandler(server.accept()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
