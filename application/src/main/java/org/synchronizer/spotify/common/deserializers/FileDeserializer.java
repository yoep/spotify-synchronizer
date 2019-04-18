package org.synchronizer.spotify.common.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.File;
import java.io.IOException;

public class FileDeserializer extends JsonDeserializer<File> {
    @Override
    public File deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        return new File(jsonParser.getValueAsString());
    }
}
