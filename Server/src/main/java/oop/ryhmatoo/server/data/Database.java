package oop.ryhmatoo.server.data;

import lombok.Getter;
import oop.ryhmatoo.server.data.storage.ChannelStorage;
import oop.ryhmatoo.server.data.storage.MessageStorage;
import oop.ryhmatoo.server.data.storage.UserStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Database {

    private final Connection connection; // Ei saanud datasorce abil tööle, kuid failist lugeva puhul vahet pole
    @Getter
    private final MessageStorage messageStorage;
    @Getter
    private final ChannelStorage channelStorage;
    @Getter
    private final UserStorage userStorage;

    public Database(String name) {
        this.loadDatabase(name);
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + name);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e); // neid ei saa esineda class on pom faili kaudu kindlalt olemas
                                           // ning sql exepioni välistab laadimine
        }
        this.messageStorage = new MessageStorage(this);
        this.channelStorage = new ChannelStorage(this);
        this.userStorage = new UserStorage(this);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void execute(SQLStatement slq, Object... values) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(slq.get())) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.execute();
        }
    }

    public ResultSet query(SQLStatement slq, Object... values) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(slq.get());
        try {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement.executeQuery();
        } finally {
            statement.closeOnCompletion();
        }
    }

    private void loadDatabase(String name) {
        File file = new File(name);
        if (file.exists() || Files.isRegularFile(Path.of(name))) return;
        try (InputStream is = this.getClass()
                .getClassLoader().getResourceAsStream("data.sqlite");
             OutputStream out = new FileOutputStream(file)) {

            byte[] buffer = is.readAllBytes();
            out.write(buffer);

        } catch (IOException e) {
            // Siin ei tohiks kunagi tekkida error.
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> queryAndMap(SQLStatement statments, Function<ResultSet, T> mapper, Object... args) throws SQLException {
        ArrayList<T> list = new ArrayList<>();
        try (ResultSet rs = this.query(statments, args)) {
            if (!rs.isBeforeFirst()) return list;
            while (rs.next()) {
                list.add(mapper.apply(rs));
            }
        }
        return list;
    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
