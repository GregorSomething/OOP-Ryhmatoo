package oop.ryhmatoo.server;

import oop.ryhmatoo.server.data.records.ServerUser;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerUserTest {

    @Test
    public void createAndCheckTest() {
        String pwd = "hehSeeHea";
        ServerUser user = ServerUser.from("Gregor", "#ffffff", pwd);
        ServerUser user2 = ServerUser.from("Gregor", "#ffffff", pwd.repeat(2));

        assertFalse(user2.isPassword(pwd));
        assertTrue(user.isPassword(pwd));
        assertTrue(user2.isPassword(pwd.repeat(2)));
    }
}
