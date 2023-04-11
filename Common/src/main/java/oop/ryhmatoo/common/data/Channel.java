package oop.ryhmatoo.common.data;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Channel(String name, boolean canWrite, List<String> members, Type channelType) {

    @SneakyThrows
    public static Channel from(ResultSet rs) {
        return new Channel(rs.getString("name"), true,
                Arrays.stream(rs.getString("members").split(";"))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()),
                Type.valueOf(rs.getString("type")));
    }

    public enum Type {
        DIRECT_MESSAGE,
        CHANNEL;
    }
}
