package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.data.ResponseHeader;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Klass mis kirjeldab üldist serveriga suhtlust.
 * Abstract et saaks kasutada kui interfacet, milles osa meetodeid defineeritud
 */
public interface ServerConnection {

    void start(ClientInfo info) throws IOException;

    ResponseHeader sendMessage(String message, String channel) throws IOException;
    List<Message> getLastMessages(int limit) throws IOException;
    void registerMessageListener(Consumer<Message> listener);

}
