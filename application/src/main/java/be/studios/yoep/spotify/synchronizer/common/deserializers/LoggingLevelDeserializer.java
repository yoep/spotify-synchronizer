package be.studios.yoep.spotify.synchronizer.common.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class LoggingLevelDeserializer extends JsonDeserializer<Level> {
    @Override
    public Level deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        parser.nextValue();
        return Level.valueOf(parser.getValueAsString());
    }
}
