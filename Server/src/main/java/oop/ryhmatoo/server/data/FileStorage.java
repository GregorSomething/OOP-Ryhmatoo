package oop.ryhmatoo.server.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

public class FileStorage {

    private static final Path path = Path.of("file_storage");

    public static void init() throws IOException {
        if (Files.isRegularFile(path))
            Files.delete(path);
        if (!Files.isDirectory(path))
            Files.createDirectories(path);
    }

    public static String write(String channel, String fileName, byte[] bytes) throws IOException {
        String[] fileNameParts = fileName.split("\\.");
        String fileExtension = fileNameParts[fileNameParts.length - 1];
        String storageName = UUID.nameUUIDFromBytes((channel + fileName + new Date().getTime())
                .getBytes(StandardCharsets.UTF_8)).toString() + "." + fileExtension;
        Files.write(path.resolve(storageName), bytes);
        return storageName;
    }

    public static byte[] read(String storageName) throws IOException {
        Path toFile = path.resolve(storageName);
        if (!Files.isRegularFile(toFile)) return new byte[0]; // Näitab viga ja saadab edasi.
        return Files.readAllBytes(toFile);
    }
}
