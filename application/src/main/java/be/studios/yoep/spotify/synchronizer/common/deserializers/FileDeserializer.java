package be.studios.yoep.spotify.synchronizer.common.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.File;
import java.io.IOException;

public class FileDeserializer extends JsonDeserializer<File> {
    @Override
    public File deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new File(jsonParser.getValueAsString());
    }
}
