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

    /**
     * Loob ühenduse serveriga
     * @param info sisselogimis andmed
     * @throws LoginException sisselogimis viga
     */
    void start(ClientInfo info) throws LoginException;

    /**
     * Saadab sõnumi info serveri
     * @param message sõnum
     * @param channel kanal
     * @return serverilt saadud vastus
     * @throws IOException
     */
    ResponseHeader sendMessage(String message, String channel) throws IOException;

    /**
     * Tagastab viimased x sõnumit, kui neid on nii palju
     * @param limit kui palju on vaja
     * @return sõnumite listi, suurus ei pruugi olla sama mis limiit
     * @throws IOException
     */
    List<Message> getLastMessages(int limit) throws IOException;
    void registerMessageListener(Consumer<Message> listener);

    public class LoginException extends Exception {
        private final String message;

        public LoginException(String message) {
            this.message = message;
        }
        @Override
        public String getMessage() {
            return message;
        }
    }
}
