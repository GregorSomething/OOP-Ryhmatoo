package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.response.LoginResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface that describes client server communication
 */
public interface ServerConnection extends AutoCloseable {

    /**
     * Method that initialise server connection, makes this interfaces implementation class
     * @param address server address in form of "ip:port", if port is missing it will be set to default (10021)
     * @return initialized Server connection object
     * @throws IllegalArgumentException if address is not valid or server on that address does not exists
     */
    static ServerConnection connect(String address) throws IllegalArgumentException {
        return new ServerConnectionImp(address);
    }

    /**
     * Checks if username and password are valid
     *
     * @param name     username
     * @param password password
     * @return true it are valid, does not do log in, for that see {@link ServerConnection#start(String, String)}
     */
    LoginResponse isValidCredentials(String name, String password);

    /**
     * Creates new user, does not do log in, for that see {@link ServerConnection#start(String, String)}
     * @param name username
     * @param password password
     * @param color chat color for this user
     * @throws LoginException if name is already in use or name/password are not valid
     */
    void createNewUser(String name, String password, String color) throws LoginException;

    /**
     * Initializes main connection
     * @param name username
     * @param password password
     * @throws LoginException if username ore password is not valid
     */
    void start(String name, String password) throws LoginException;

    /**
     * Gets currently active users, only names
     * @return currently active/connected to server users.
     */
    List<String> getActiveUsers();

    /**
     * Gets all chats for this user
     * @return all chats for this user.
     */
    List<Channel> getChats();

    /**
     * Gets last messages in channel
     * @param limit how many messages
     * @param channel in what channal where requested messages
     * @return list of messages from that channel, NB! limit might not equal size of list
     * @throws IllegalArgumentException thrown if channel did not exsist or limit was less than 0
     */
    List<Message> getLastMessages(int limit, String channel) throws IllegalArgumentException;

    /**
     * Sends message to server
     * @param channel channel where the message was sent from, if channel does not exsist in server this will fail quietly
     * @param content message content, String
     */
    void sendMessage(String channel, String content);

    /**
     * Sends file to server
     * @param channel channel where the file was sent from, if channel does not exsist in server this will fail quietly
     * @param file file reference
     * @param type file type, not MESSAGE
     * @throws IOException exception that was thrown, when tying to send file to server
     * @throws IllegalArgumentException if file does not exist, or if file is bigger than 8 Mb
     */
    void sendFile(String channel, File file, Message.Type type) throws IOException, IllegalArgumentException;

    /**
     * Gets file from message
     * @param refMessage message that has the file
     * @return file object
     */
    File getFile(Message refMessage);

    /**
     * Creates new channel
     * @param name name of the channel must be unique
     * @param members members of this channel
     * @param type channel type
     */
    void createNewChannel(String name, List<String> members, Channel.Type type);

    /**
     * Registers message listener
     * @param listener ...
     */
    void registerMessageListener(Consumer<Message> listener);

    /**
     * Registers channel listener, when new channel involving this user was created
     * @param listener ...
     */
    void registerChannelListener(Consumer<Channel> listener);

    /**
     *
     * @param listener
     */
    void registerUserListener(Consumer<String> listener);

    /**
     * Returns all users
     * @return usernames
     */
    List<String> getAllUsers();


    class LoginException extends Exception {

        public LoginException(String message) {
            super(message);
        }
    }
}
