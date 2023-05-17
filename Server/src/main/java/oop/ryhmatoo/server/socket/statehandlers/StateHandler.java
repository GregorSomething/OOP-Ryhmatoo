package oop.ryhmatoo.server.socket.statehandlers;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.util.HashMap;

public class StateHandler {

    private final HashMap<SocketHolder.State, SocketStateHandler> handlerMap;
    private final ReadHandler readHandler;

    public StateHandler() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(SocketHolder.State.CONNECTED, new ConnectionHandler());
        this.readHandler = new ReadHandler();
        this.handlerMap.put(SocketHolder.State.READING_SOCKET, readHandler);
        this.handlerMap.put(SocketHolder.State.WRITING_SOCKET, new WriteHandler());
    }


    public void handel(int code, SocketHolder socket) {
        boolean ok = this.handlerMap.get(socket.getState()).handel(code, socket);
        if (!ok) {
            Server.LOG.warning(String.format("Failed to handel code %d, on user %s in state %s.",
                    code, socket.getUser().name(), socket.getState()));
        }
    }

    public void sendMessage(Message message) {
        this.readHandler.sendMessage(message);
    }

    public void sendChannel(Channel channel) {
        this.readHandler.sendChannel(channel);
    }

    public void sendUser(String user) {
        this.readHandler.sendUser(user);
    }
}
