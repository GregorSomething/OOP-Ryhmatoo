package oop.ryhmatoo.common.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class JSONHelper {

    @Getter
    private final ObjectMapper mapper;

    public JSONHelper() {
        this.mapper = new ObjectMapper();
    }

    public <T> String getListAsJSON(List<T> list) throws JsonProcessingException {
        return this.mapper.writeValueAsString(list);
    }

    public <T> List<T> getListFromJSON(String json, Class<T> objClass) throws JsonProcessingException {
        return this.mapper.readValue(json,
                this.mapper.getTypeFactory()
                        .constructCollectionType(List.class, objClass));
    }

    public <T> T readObjectFrom(DataInputStream dis, Class<T> type) throws IOException {
        String json = dis.readUTF();
        return this.mapper.readValue(json, type);
    }
}
