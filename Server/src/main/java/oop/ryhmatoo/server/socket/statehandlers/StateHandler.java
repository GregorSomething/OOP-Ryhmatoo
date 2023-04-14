package oop.ryhmatoo.server.socket.statehandlers;

import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.util.HashMap;

public class StateHandler {

    private final HashMap<SocketHolder.State, SocketStateHandler> handlerMap;

    public StateHandler() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(SocketHolder.State.CONNECTED, new ConnectionHandler());
        this.handlerMap.put(SocketHolder.State.READING_SOCKET, new ReadHandler());
        this.handlerMap.put(SocketHolder.State.WRITING_SOCKET, new WriteHandler());
    }


    public void handel(int code, SocketHolder socket) {
        boolean ok = this.handlerMap.get(socket.getState()).handel(code, socket);
        if (!ok) {
            System.out.printf("Failed to handel code %d, on user %s in state %s.", code, socket.getUsername(), socket.getState());
        }
    }
}
