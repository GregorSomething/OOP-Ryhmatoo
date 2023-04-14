package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.CreateNewUserRequest;
import oop.ryhmatoo.common.socket.request.ValidCredentialsRequest;
import oop.ryhmatoo.common.socket.response.LoginResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Test {

    public static void main(String[] args) throws IOException {
        JSONHelper helper = new JSONHelper();
        try (Socket socket = new Socket("127.0.0.1", 10021)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            /*int code = 101;
            CreateNewUserRequest request = new CreateNewUserRequest("test", "parool1", "#ffffff");
            dos.writeInt(code);
            dos.writeUTF(helper.getMapper().writeValueAsString(request));
            int res = dis.readInt();
            LoginResponse response = helper.readObjectFrom(dis, LoginResponse.class);
            System.out.println(response);*/

            int code = 100;
            ValidCredentialsRequest request = new ValidCredentialsRequest("test", "parool1");

            dos.writeInt(code);
            dos.writeUTF(helper.getMapper().writeValueAsString(request));
            int res = dis.readInt();
            LoginResponse response = helper.readObjectFrom(dis, LoginResponse.class);
            System.out.println(response);

            dos.writeInt(103);
            dos.writeUTF(helper.getMapper().writeValueAsString(request));
            int res2 = dis.readInt();
            LoginResponse response2 = helper.readObjectFrom(dis, LoginResponse.class);
            System.out.println(response2);

            dos.writeInt(120);
            dos.writeUTF(helper.getMapper().writeValueAsString(
                    new Message("Gregor", "kanal1", "Terekest cliendilt", 123, Message.Type.MESSAGE)));
            dos.writeInt(109); // Disconnect code
        }
    }
}
