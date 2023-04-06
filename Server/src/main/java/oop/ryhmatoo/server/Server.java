package oop.ryhmatoo.server;

public class Server {
    private static Server instance;

    public Server(String[] args) {
        Server.instance = this;
    }

    public static void main(String[] args) {

    }

    public static Server getInstance() {
        return instance;
    }
}
