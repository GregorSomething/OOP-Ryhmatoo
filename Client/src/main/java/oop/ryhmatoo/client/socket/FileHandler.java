package oop.ryhmatoo.client.socket;

import oop.ryhmatoo.common.data.Message;
import oop.ryhmatoo.common.socket.JSONHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    private static final Path path = Path.of(".tmp_file_storage");

    public FileHandler() throws IOException {
        if (Files.isRegularFile(path))
            Files.delete(path);
        if (!Files.isDirectory(path))
            Files.createDirectories(path);
    }

    public void send(String channel, Path toFile, Message.Type type, DataOutputStream dos, JSONHelper helper) throws IOException, IllegalArgumentException {
        if (!Files.isRegularFile(toFile)) throw
                new IllegalArgumentException(toFile.toString() + " is not a regular file or is not there");
        if (Files.size(toFile) > 20 * 1024L * 2024L)
            throw new IllegalArgumentException("File to large > 8 Mb");

        Message m = new Message("", "", channel, toFile.getFileName().toString(), 1, type);
        byte[] data = Files.readAllBytes(toFile);

        dos.writeInt(122);
        dos.writeUTF(helper.getMapper().writeValueAsString(m));
        dos.writeInt(data.length);
        dos.write(data);
    }

    public File getFile(String storageName, DataInputStream dis, DataOutputStream dos) throws IOException {
        if (Files.isRegularFile(path.resolve(storageName)))
            return path.resolve(storageName).toFile();
        dos.writeInt(113);
        dos.writeUTF(storageName);
        int code = dis.readInt();
        int len = dis.readInt();
        byte[] data = dis.readNBytes(len);
        if (len == 0) return null;
        Files.write(path.resolve(storageName), data);
        return path.resolve(storageName).toFile();
    }
}
