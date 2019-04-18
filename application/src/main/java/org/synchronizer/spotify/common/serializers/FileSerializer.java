package org.synchronizer.spotify.common.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.File;
import java.io.IOException;

public class FileSerializer extends JsonSerializer<File> {

    @Override
    public void serialize(File value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getAbsolutePath());
    }
}
