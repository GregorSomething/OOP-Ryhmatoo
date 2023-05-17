package oop.ryhmatoo.server.socket.statehandlers;

import oop.ryhmatoo.common.socket.JSONHelper;
import oop.ryhmatoo.common.socket.request.CreateNewUserRequest;
import oop.ryhmatoo.common.socket.request.ValidCredentialsRequest;
import oop.ryhmatoo.common.socket.response.LoginResponse;
import oop.ryhmatoo.server.Server;
import oop.ryhmatoo.server.data.records.ServerUser;
import oop.ryhmatoo.server.data.storage.UserStorage;
import oop.ryhmatoo.server.socket.SocketHolder;
import oop.ryhmatoo.server.socket.SocketStateHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ConnectionHandler implements SocketStateHandler {

    private final JSONHelper jsonHelper;
    private final UserStorage userStorage;

    public ConnectionHandler() {
        this.jsonHelper = Server.getInstance().getJSONHelper();
        this.userStorage = Server.getInstance().getDatabase().getUserStorage();
    }

    @Override
    public boolean handel(int code, SocketHolder socket) {
        return switch (code) {
            case  100 -> this.handelValidCredentialsRequest(socket);
            case  101 -> this.handelCreateNewUserRequest(socket);
            case  102 -> this.handelLoginAndRead(socket);
            case  103 -> this.handelLoginAndWrite(socket);
            default -> throw new IllegalStateException("Unexpected request code: " + code);
        };
    }

    private boolean handelValidCredentialsRequest(SocketHolder socket) {
        try {
            ValidCredentialsRequest request = this.jsonHelper.readObjectFrom(socket.getDataInputStream(), ValidCredentialsRequest.class);
            LoginResponse response = this.isValid(request);
            // Seab vastuse vastuse
            this.write(200, socket.getDataOutputStream(), response);

            return true;
        } catch (IOException e) {
            Server.LOG.warning("Viga requesti käsitlemisel. handelValidCredentialsRequest " + e.getMessage());
            return false;
        }
    }

    private boolean handelCreateNewUserRequest(SocketHolder socket) {
        try {
            CreateNewUserRequest request = this.jsonHelper.readObjectFrom(socket.getDataInputStream(), CreateNewUserRequest.class);
            ServerUser user = this.userStorage.getUser(request.name());

            // Kui kasutaja on olemas
            if (user != null) {
                this.write(200, socket.getDataOutputStream(),
                        new LoginResponse(false, "Nimi on juba kasutusel!"));
            }

            ServerUser newUser = ServerUser.from(request);
            this.userStorage.saveNewUser(newUser);
            this.write(200, socket.getDataOutputStream(),
                    new LoginResponse(true, "Kasutaja loodud"));

            return true;
        } catch (SQLException | IOException e) {
            Server.LOG.warning("Viga requesti käsitlemisel. handelCreateNewUserRequest " + e.getMessage());
            return false;
        }
    }

    private boolean handelLoginAndRead(SocketHolder socket) {
        return this.handelStateChange(socket, SocketHolder.State.READING_SOCKET);
    }

    private boolean handelLoginAndWrite(SocketHolder socket) {
        return this.handelStateChange(socket, SocketHolder.State.WRITING_SOCKET);
    }

    private LoginResponse isValid(ValidCredentialsRequest request) {
        try {
            ServerUser user = this.userStorage.getUser(request.name());
            if (user == null)
                return new LoginResponse(false, "Kasutaja nimi on vale!");
            if (!user.isPassword(request.password()))
                return new LoginResponse(false, "Kasutaja parool on vale");
            return new LoginResponse(true, "OK");
        } catch (SQLException e) {
            return new LoginResponse(false, "Andmebaasi viga! " + e.getMessage());
        }
    }

    private boolean handelStateChange(SocketHolder socket, SocketHolder.State newState) {
        try {
            ValidCredentialsRequest request = this.jsonHelper.readObjectFrom(socket.getDataInputStream(), ValidCredentialsRequest.class);
            LoginResponse response = this.isValid(request);

            // Kui parool ja nimi oli õige, muuda state. Seab ka kasutaja nime.
            if (response.valid()) {
                if (socket.getState() == SocketHolder.State.CONNECTED)
                    Server.getInstance().getSockets().getStateHandler().sendUser(request.name());
                socket.setState(newState);
                socket.setUser(userStorage.getUser(request.name()));
                Server.LOG.info(String.format("Socket %s logis sisse.", socket.toString()));
            }

            // Seab vastuse tüüp koodi
            socket.getDataOutputStream().writeInt(200);
            // Kirjuta vastus tagasi
            socket.getDataOutputStream().writeUTF(jsonHelper
                    .getMapper().writeValueAsString(response));
            return true;
        } catch (IOException | SQLException e) {
            Server.LOG.warning("Viga requesti käsitlemisel. handelValidCredentialsRequest " + e.getMessage());
            return false;
        }
    }

    private void write(int code, DataOutputStream dos, Object object) throws IOException {
        dos.writeInt(code);
        dos.writeUTF(jsonHelper
                .getMapper().writeValueAsString(object));
    }
}
