package oop.ryhmatoo.server;

import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.server.data.Database;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {

    public static Database database;

    @BeforeClass
    public static void setUp() {
        database = new Database("test.sqlite");
    }

    @AfterClass
    public static void destroy() throws IOException {
        database.close();
        Files.delete(Path.of("test.sqlite"));
    }

    @Test
    public void channelReadWrite() throws SQLException {
        Channel channel = new Channel("Test", true, List.of("tu1", "tu2", "tu3"), Channel.Type.DIRECT_MESSAGE);
        database.getChannelStorage().saveNewChannel(channel);

        List<Channel> channelList = database.getChannelStorage().getChannelsForUser("tu1");
        assertEquals(1, channelList.size());
        assertEquals("Test", channelList.get(0).name());
        assertEquals(Channel.Type.DIRECT_MESSAGE, channelList.get(0).type());
    }

    @Test
    public void messageReadWrite() throws SQLException {
        Message message = new Message("tu1", "Test", "Terekest", 12345, Message.Type.MESSAGE);
        database.getMessageStorage().saveMessage(message);

        List<Message> messages = database.getMessageStorage().getMessagesInChannelLimit("Test", 1000);
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

}
