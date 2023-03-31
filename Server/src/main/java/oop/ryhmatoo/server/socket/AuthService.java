package oop.ryhmatoo.server.socket;

import oop.ryhmatoo.server.socket.data.ClientInfo;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final Map<String, String> userData;

    public AuthService() {
        // TODO Asendada mitte inmemory hoidlaga
        this.userData = new HashMap<>();
        userData.put("test", "parool1");
        userData.put("test1", "parool2");
    }

    /**
     * Kontrollib kas kasutajal on korrektne parool
     * @param client kliendi info mida kontrollida
     * @return true kui parool oli õige.
     */
    public boolean hasCorrectPassword(ClientInfo client) {
        // TODO Asendada mitte inmemory hoidlaga
        return userData.containsKey(client.username())
                && userData.get(client.username()).equals(client.password());
    }
}
