package org.synchronizer.spotify.common;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import org.apache.logging.log4j.Level;
import org.synchronizer.spotify.common.deserializers.FileDeserializer;
import org.synchronizer.spotify.common.deserializers.LoggingLevelDeserializer;
import org.synchronizer.spotify.common.deserializers.UserSettingsDeserializer;
import org.synchronizer.spotify.common.serializers.FileSerializer;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.spotify.AlbumTypeDeserializer;
import org.synchronizer.spotify.spotify.api.v1.AlbumType;

import java.io.File;

public final class SynchronizerModule extends SimpleModule {
    public SynchronizerModule() {
        super("Spotify synchronization module", PackageVersion.VERSION);

        addDeserializer(AlbumType.class, new AlbumTypeDeserializer());
        addDeserializer(Level.class, new LoggingLevelDeserializer());
        addDeserializer(File.class, new FileDeserializer());
        addDeserializer(UserSettings.class, new UserSettingsDeserializer());
        addSerializer(File.class, new FileSerializer());
    }
}
