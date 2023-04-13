package oop.ryhmatoo.server.data.records;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.util.Arrays;

public record ServerUser(String name, String color, byte[] salt, byte[] hashedPassword) {

    public boolean isPassword(String password) {
        return Arrays.equals(this.hashedPassword, ServerUser.hashPassword(password, salt));
    }

    @SneakyThrows
    public static ServerUser from(ResultSet rs) {
        return new ServerUser(rs.getString("name"),
                rs.getString("color"),
                rs.getBytes("salt"),
                rs.getBytes("password"));
    }

    public static ServerUser from(String name, String color, String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return new ServerUser(name, color, salt, ServerUser.hashPassword(password, salt));
    }

    @SneakyThrows // Ma ei oleks seda niigi käsitlenud
    public static byte[] hashPassword(String password, byte[] salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
