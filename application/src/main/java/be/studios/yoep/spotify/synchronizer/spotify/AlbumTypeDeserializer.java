package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.spotify.api.v1.AlbumType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AlbumTypeDeserializer extends JsonDeserializer<AlbumType> {
    @Override
    public AlbumType deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        return AlbumType.valueOf(parser.getValueAsString().toUpperCase());
    }
}
