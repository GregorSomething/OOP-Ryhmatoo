package oop.ryhmatoo.common.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Andmeklass mis kirjeldab sõnumi
 * @param sender saatja displayName
 * @param channel kanal, kus sõnum saadeti
 * @param content sõnumi sisu
 * @param timestamp saatmisaeg
 */
public record Message(String sender, String channel, String content, long timestamp) implements Comparable<Message> {

    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sender);
        dos.writeUTF(channel);
        dos.writeUTF(content);
        dos.writeLong(timestamp);
    }

    public static Message from(DataInputStream dis) throws IOException {
        String sender = dis.readUTF();
        String channel = dis.readUTF();
        String content = dis.readUTF();
        long timestamp = dis.readLong();
        return new Message(sender, channel, content, timestamp);
    }

    @Override
    public int compareTo(Message o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
