package oop.ryhmatoo.server.data;

import oop.ryhmatoo.server.Server;
import org.sqlite.SQLiteDataSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private final SQLiteDataSource dataSource;

    public Database(String name) {
        this.loadDatabase(name);
        this.dataSource = new SQLiteDataSource();
        this.dataSource.setUrl("jdbc:sqlite:" + name);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void execute(SQLStatement slq, Object... values) throws SQLException {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(slq.get())) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.execute();
        }
    }

    public ResultSet query(SQLStatement slq, Object... values) throws SQLException {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(slq.get())) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement.executeQuery();
        }
    }

    private void loadDatabase(String name) {
        File file = new File(name);
        if (file.exists() || Files.isRegularFile(Path.of(name))) return;
        try (InputStream is = Server.getInstance().getClass()
                .getClassLoader().getResourceAsStream("data.sqlite");
             OutputStream out = new FileOutputStream(file)) {

            byte[] buffer = is.readAllBytes();
            out.write(buffer);

        } catch (IOException e) {
            // Siin ei tohiks kunagi tekkida error.
            throw new RuntimeException(e);
        }
    }

}
