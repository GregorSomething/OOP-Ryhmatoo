package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.ValidCredentialsRequest;
import oop.ryhmatoo.common.socket.response.LoginResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketConnection {

    protected final Socket socket;
    protected final DataInputStream dataInputStream;
    protected final DataOutputStream dataOutputStream;
    protected final JSONHelper jsonHelper;


    public AbstractSocketConnection(String address, int port, JSONHelper jsonHelper) throws IOException {
        this.jsonHelper = jsonHelper;
        this.socket = new Socket(address, port);
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public LoginResponse tryLogin(int code, String name, String password) throws IOException {
        ValidCredentialsRequest cred = new ValidCredentialsRequest(name, password);
        this.dataOutputStream.writeInt(code);
        this.dataOutputStream.writeUTF(jsonHelper.getMapper().writeValueAsString(cred));
        int resCode = this.dataInputStream.readInt();
        if (resCode != 200) throw new RuntimeException("Vale resCode serverilt!!!");
        return jsonHelper.readObjectFrom(this.dataInputStream, LoginResponse.class);
    }

    public void close() throws IOException {
        this.dataOutputStream.writeInt(109);
        this.dataInputStream.close();
        this.dataOutputStream.close();
        this.socket.close();
    }
}
