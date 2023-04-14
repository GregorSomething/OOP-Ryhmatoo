package oop.ryhmatoo.server.socket;

import java.io.IOException;

public interface SocketStateHandler {

    boolean handel(int code, SocketHolder socket);
}
