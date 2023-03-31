package oop.ryhmatoo.common.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record ResponseHeader(int status, String message) {

    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(status);
        dos.writeUTF(message);
    }

    public static ResponseHeader from(DataInputStream dis) throws IOException {
        int status = dis.readInt();
        String message = dis.readUTF();
        return new ResponseHeader(status, message);
    }
}
