package oop.ryhmatoo.server;

import oop.ryhmatoo.client.socket.ServerConnection;
import oop.ryhmatoo.common.data.Channel;
import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.response.LoginResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerTest {

    public static Server server;
    private static ServerConnection sclient;

    @BeforeClass
    public static void setup() throws IOException, InterruptedException {
        ServerTest.server = new Server("test.sqlite", 10021);
        Thread.sleep(500);
    }

    @AfterClass
    public static void destroy() throws IOException {
        Files.delete(Path.of("test.sqlite"));
    }

    @Test // Rumal nimi selleks et see jookseks esimesena vt rida ~16
    public void AAA__newUserAndValid() throws Exception {
        try (ServerConnection conn = ServerConnection.connect("127.0.0.1:10021")) {
            conn.createNewUser("Gregor", "parool", "fffffff");
            LoginResponse res = conn.isValidCredentials("Gregor", "parool");
            assertTrue(res.valid());
        }
    }

    @Test
    public void BBB__loginFailTest() {
         sclient = ServerConnection.connect("127.0.0.1:10021");
         assertThrows(ServerConnection.LoginException.class, () -> sclient.start("pole", "aaa"));
    }

    @Test
    public void CCC__login() throws ServerConnection.LoginException {
        this.sclient.start("Gregor", "parool");
    }

    @Test
    public void DDD_channelTest() {
        sclient.createNewChannel("Jama", List.of("test", "Gregor", "kanad"), Channel.Type.CHANNEL);

        assertNotNull(sclient.getChats().stream().filter(channel -> channel.name().equals("Jama")).findAny().orElse(null));
    }

    @Test
    public void EEE__messageTest() {
        sclient.createNewChannel("Jama2", List.of("test", "Gregor", "kanad"), Channel.Type.CHANNEL);
        sclient.sendMessage("Jama", "Terekest cliendilt");
        sclient.sendMessage("Jama", "Terekest cliendilt2");
        sclient.sendMessage("Jama2", "Terekest cliendilt3");

        assertEquals(2, sclient.getLastMessages(100, "Jama").size());
        List<Message> jama2 = sclient.getLastMessages(100, "Jama2");
        assertEquals(1, jama2.size());
        assertEquals("Terekest cliendilt3", jama2.get(0).content());
    }

    @Test
    public void FFF_activeUsers() throws Exception {
        List<String> users = sclient.getActiveUsers();
        assertEquals(1, users.size());
        assertEquals("Gregor", users.get(0));
        sclient.close();
    }
}
